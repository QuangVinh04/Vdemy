package V1Learn.spring.schedule;

import V1Learn.spring.entity.Checkout;
import V1Learn.spring.entity.Payment;
import V1Learn.spring.enums.CheckoutState;
import V1Learn.spring.enums.OrderStatus;
import V1Learn.spring.enums.PaymentStatus;
import V1Learn.spring.repository.CheckoutRepository;
import V1Learn.spring.repository.PaymentRepository;
import V1Learn.spring.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutCleanupJob {

    CheckoutRepository checkoutRepository;
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;

    /**
     * Mỗi 5 phút: Expire checkout PENDING/PAYMENT_PROCESSING quá 30 phút
     * + Cancel payment PENDING quá hạn
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    @Transactional
    public void expireStaleCheckoutsAndPayments() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        // 1. Expire checkout PENDING/PAYMENT_PROCESSING quá 30 phút
        List<CheckoutState> activeStates = List.of(
                CheckoutState.PENDING, CheckoutState.PAYMENT_PROCESSING);

        List<Checkout> staleCheckouts = checkoutRepository
                .findByCheckoutStateInAndCreatedAtBefore(activeStates, thirtyMinutesAgo);

        for (Checkout checkout : staleCheckouts) {
            checkout.setCheckoutState(CheckoutState.EXPIRED);
            checkoutRepository.save(checkout);
        }

        if (!staleCheckouts.isEmpty()) {
            log.info("Expired {} stale checkouts", staleCheckouts.size());
        }

        // 2. Cancel payment PENDING đã hết hạn (expiredAt < now)
        List<Payment> expiredPayments = paymentRepository
                .findByStatusAndExpiredAtBefore(PaymentStatus.PENDING, LocalDateTime.now());

        for (Payment payment : expiredPayments) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            // Cancel order liên quan
            if (payment.getOrder() != null
                    && payment.getOrder().getStatus() == OrderStatus.PENDING) {
                payment.getOrder().setStatus(OrderStatus.CANCELLED);
                orderRepository.save(payment.getOrder());
            }
        }

        if (!expiredPayments.isEmpty()) {
            log.info("Failed {} expired payments", expiredPayments.size());
        }
    }

    /**
     * Mỗi ngày lúc 3:00 AM: Xóa checkout EXPIRED quá 7 ngày
     */
    @Scheduled(cron = "0 0 3 * * *") // 3:00 AM mỗi ngày
    @Transactional
    public void deleteOldExpiredCheckouts() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<Checkout> oldCheckouts = checkoutRepository
                .findByCheckoutStateAndUpdatedAtBefore(CheckoutState.EXPIRED, sevenDaysAgo);

        if (!oldCheckouts.isEmpty()) {
            checkoutRepository.deleteAll(oldCheckouts);
            log.info("Deleted {} expired checkouts older than 7 days", oldCheckouts.size());
        }
    }
}
