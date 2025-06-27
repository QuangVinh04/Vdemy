package V1Learn.spring.controller;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.NotificationResponse;
import V1Learn.spring.Service.NotificationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationController {
    NotificationService notificationService;

    /**
     * Đánh dấu một thông báo là đã đọc
     */
    @PutMapping("/{id}/read")
    public APIResponse<?> markNotificationAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return APIResponse.builder()
                .result(true)
                .build();
    }

    @GetMapping("/notification-current")
    public APIResponse<List<NotificationResponse>> getCurrentNotification() {

        var result = notificationService.getNotificationsUser();

        return APIResponse.<List<NotificationResponse>>builder()
                .result(result)
                .build();
    }

    /**
     * Đánh dấu tất cả thông báo là đã đọc
     */
    @PutMapping("/read-all")
    public APIResponse<?> markAllNotificationsAsRead() {
        log.info("mark all notifications as read");
        notificationService.markAllAsReadForUser();
        return APIResponse.builder()
                .result(true)
                .build();
    }

    /**
     * Xoá thông báo
     */
    @DeleteMapping("/{id}")
    public APIResponse<String> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return APIResponse.<String>builder()
                .result("Notification has been deleted")
                .build();
    }
}

