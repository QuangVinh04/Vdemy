package V1Learn.spring.Service;

import V1Learn.spring.DTO.Request.ReplyReviewRequest;
import V1Learn.spring.DTO.Response.NotificationResponse;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.ReviewResponse;
import V1Learn.spring.DTO.event.NotificationEvent;
import V1Learn.spring.DTO.event.ReviewRequest;
import V1Learn.spring.Entity.Course;
import V1Learn.spring.Entity.Notification;
import V1Learn.spring.Entity.Review;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.ReviewMapper;
import V1Learn.spring.Repostiory.CourseRepository;
import V1Learn.spring.Repostiory.NotificationRepository;
import V1Learn.spring.Repostiory.ReviewRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.utils.NotificationType;
import V1Learn.spring.utils.SecurityUtils;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    UserRepository userRepository;
    NotificationRepository notificationRepository;
    SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void saveNotification(NotificationEvent event, String userId) {

        User recipient = userRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!Objects.equals(recipient.getId(), userId)){
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(event.getType())
                .title(event.getTitle())
                .url(event.getTargetUrl())
                .message(event.getContent())
                .isRead(false)
                .build();

        simpMessagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
        notificationRepository.save(notification);
    }


    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> getNotificationsUser() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByIdDesc(user.getId());

        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }

        return notifications.stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .url(notification.getUrl())
                        .isRead(notification.getIsRead())
                        .timestamp(notification.getCreatedAt())
                        .build())
                .toList();
    }


    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));

        String userId = SecurityUtils.getCurrentUserId()
                        .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        if(!Objects.equals(notification.getRecipient().getId(), userId)){
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        // Đánh dấu đã đọc
        if (Boolean.FALSE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void markAllAsReadForUser() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Notification> unread = notificationRepository.findAllByRecipientIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        if(!Objects.equals(notification.getRecipient().getId(), userId)){
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }
        notificationRepository.deleteById(notificationId);
    }





}
