package V1Learn.spring.service.listener;


import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.dto.event.PaymentCompleteEvent;
import V1Learn.spring.dto.event.PaymentResultEvent;
import V1Learn.spring.entity.Notification;
import V1Learn.spring.enums.NotificationType;
import V1Learn.spring.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentListener {

    SimpMessagingTemplate messagingTemplate;
    ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompleteEvent event) {
        log.info("Processing notifications for payment {}: {}", event.getTransactionId(), event.getStatus());

        try {
            // 1. Gửi WebSocket báo trạng thái ngay lập tức cho UI
            String wsResult = event.getStatus() == PaymentStatus.COMPLETED ? "SUCCESS" : "FAILED";
            messagingTemplate.convertAndSend(
                    "/topic/payment/" + event.getUserId(),
                    new PaymentResultEvent(event.getTransactionId(), wsResult)
            );

            if (event.getStatus() == PaymentStatus.COMPLETED) {
                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .recipientId(event.getUserId())
                        .title("Thanh toán thành công")
                        .content("Đơn hàng " + event.getOrderCode() + " đã được thanh toán.")
                        .type(NotificationType.ORDER_CONFIRMED)
                        .createdAt(LocalDateTime.now())
                        .build();
                eventPublisher.publishEvent(notificationEvent);

            }
        } catch (Exception e) {
            // Log lỗi nhưng không làm ảnh hưởng đến luồng payment đã hoàn tất
            log.error("Lỗi khi xử lý thông báo thanh toán cho đơn {}: {}", event.getOrderCode(), e.getMessage());
        }
    }
}
