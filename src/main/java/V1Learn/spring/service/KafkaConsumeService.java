package V1Learn.spring.service;

import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.dto.event.ReviewRequest;

import V1Learn.spring.enums.NotificationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumeService {

    NotificationService notificationService;
    ReviewService reviewService;

    @KafkaListener(topics = "course-review",
            groupId = "review-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeReview(@Payload ReviewRequest request,
                              @Header(KafkaHeaders.RECEIVED_KEY) String key, // userId
                              Acknowledgment ack) {
        log.info("Received review: {}", request);

        try {
            reviewService.saveReview(request, key);
            ack.acknowledge();
        } catch (Exception e) {
            // không commit => Kafka sẽ gửi lại message này lần sau
            log.error("Error processing review", e);
        }
    }


    @KafkaListener(topics = "notification-events",
            groupId = "course-review-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeCourseReview(@Payload NotificationEvent event,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key, // userId
                                    Acknowledgment ack) {
        log.info("Received notification course review: {}", event);
        if (event.getType() != NotificationType.COURSE_REVIEW) {
            log.info("Skip message because not course review type, actual type = {}", event.getType());
            ack.acknowledge();
            return;
        }
        try {
            notificationService.saveNotification(event, key);
            ack.acknowledge();
        } catch (Exception e) {
            // không commit => Kafka sẽ gửi lại message này lần sau
            log.error("Error processing review", e);
        }
    }

    @KafkaListener(topics = "notification-events",
            groupId = "review-reply-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeReviewReply(@Payload NotificationEvent event,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key, // userId
                                   Acknowledgment ack) {
        log.info("Received notification review reply: {}", event);
        if (event.getType() != NotificationType.REVIEW_REPLY) {
            log.info("Skip message because not review reply type, actual type = {}", event.getType());
            ack.acknowledge();
            return;
        }
        try {
            notificationService.saveNotification(event, key);
            ack.acknowledge();
        } catch (Exception e) {
            // không commit => Kafka sẽ gửi lại message này lần sau
            log.error("Error processing review", e);
        }
    }

    @KafkaListener(topics = "notification-events", groupId = "order-confirmed-group")
    public void consumeOrderConfirmed(@Payload NotificationEvent event,
                                      @Header(KafkaHeaders.RECEIVED_KEY) String key, // userId
                                      Acknowledgment ack) {
        log.info("Received notification oder confirm: {}", event);
        if (event.getType() != NotificationType.ORDER_CONFIRMED) {
            log.info("Skip message because not oder confirmed type, actual type = {}", event.getType());
            ack.acknowledge();
            return;
        }
        try {
            notificationService.saveNotification(event, key);
            ack.acknowledge();
        } catch (Exception e) {
            // không commit => Kafka sẽ gửi lại message này lần sau
            log.error("Error processing review", e);
        }
    }


    @KafkaListener(topics = "notification-events",
            groupId = "register-teacher-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeRegisterTeacher(@Payload NotificationEvent event,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key, // userId
                                       Acknowledgment ack) {
        log.info("Received notification register-teacher: {}", event);
        if (event.getType() != NotificationType.REGISTER_TEACHER) {
            log.info("Skip message because not register teacher type, actual type = {}", event.getType());
            ack.acknowledge();
            return;
        }
        try {
            notificationService.saveNotification(event, key);
            ack.acknowledge();
        } catch (Exception e) {
            // không commit => Kafka sẽ gửi lại message này lần sau
            log.error("Error processing review", e);
        }
    }

}


// Ở Producer: (REST controller / service)
//Bạn có toàn quyền lấy được userId từ SecurityUtils.getCurrentUser()
// – không vấn đề.
//
// Ở Consumer Kafka:
//Consumer chạy nền (background) –
// độc lập với request, không có SecurityContext.
//
//Vì vậy trong Kafka Consumer,
// bạn không thể dùng SecurityUtils.getCurrentUser(),
// vì SecurityContextHolder sẽ rỗng –
// không có ai login cả, do Kafka listener không phải xử lý HTTP request.


