package V1Learn.spring.service.listener;

import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationListener {

    NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationEvent event) {
        try{
            notificationService.saveNotification(event, event.getRecipientId());
        } catch (Exception e){
            log.error("CẢNH BÁO: Gửi thông báo thất bại cho User {}",
                    event.getRecipientId(), e);
        }
    }



}
