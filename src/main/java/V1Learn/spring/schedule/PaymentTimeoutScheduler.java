//package V1Learn.spring.schedule;
//
//
//import V1Learn.spring.Entity.Enrollment;
//import V1Learn.spring.Repostiory.EnrollmentRepository;
//import V1Learn.spring.Service.MailService;
//import V1Learn.spring.Service.PaymentService;
//import V1Learn.spring.utils.EnrollmentStatus;
//import jakarta.mail.MessagingException;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.UnsupportedEncodingException;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.temporal.Temporal;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class PaymentTimeoutScheduler {
//    EnrollmentRepository enrollmentRepository;
//    PaymentService paymentService;
//    MailService mailService;
//
//
//    @Scheduled(cron = "0 0 0 7 * *") // mỗi 1 tiếng
//    public void checkPendingPayments() throws MessagingException, UnsupportedEncodingException {
//        log.info("Checking pending payments: {}", LocalDateTime.now());
//        LocalDateTime now = LocalDateTime.now();
//        List<Enrollment> pendingEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.PENDING);
//
//        for (Enrollment e : pendingEnrollments) {
//            Duration duration = Duration.between(e.getCreatedAT(), now);
//            if (duration.toMinutes() >= 30) {
//                // Hủy đơn
//                paymentService.cancelEnrollment(e.getId());
//
//                log.info("Enrollment {} canceled due to timeout", e.getId());
//            } else if (duration.toMinutes() == 25) {
//                // Gửi email nhắc
//                mailService.sendPaymentReminder(e.getUser().getEmail(), e.getCourse().getTitle());
//            }
//        }
//    }
//}
//
