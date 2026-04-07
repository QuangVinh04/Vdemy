package V1Learn.spring.service.listener;


import V1Learn.spring.dto.event.SendMailEvent;
import V1Learn.spring.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailListener {
    MailService mailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendMailEvent(SendMailEvent event) {
        try {
            log.info("Bắt đầu gửi email tới: {}", event.getEmail());
            mailService.sendConfirmLink(event.getEmail(), event.getOtpCode());

            log.info("Đã gửi email thành công tới: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Lỗi khi gửi email tới {}: {}", event.getEmail(), e.getMessage());
        }
    }
}
