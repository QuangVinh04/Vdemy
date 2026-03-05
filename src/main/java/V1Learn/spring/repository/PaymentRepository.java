package V1Learn.spring.repository;

import V1Learn.spring.entity.Payment;
import V1Learn.spring.enums.PaymentStatus;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Tìm payments theo userId thông qua Order relationship
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Payment> findByOrderUserIdOrderByCreatedAtDesc(@Param("userId") String userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Khóa bản ghi để các luồng khác phải đợi
    @Query("SELECT p FROM Payment p WHERE p.transactionId = :id")
    Optional<Payment> findByTransactionIdWithLock(String id);

    Optional<Payment> findByOrderCheckoutIdAndStatus(String checkoutId, PaymentStatus paymentStatus);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    // Scheduled Job: tìm payment PENDING đã hết hạn
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.expiredAt < :now")
    List<Payment> findByStatusAndExpiredAtBefore(
            @Param("status") PaymentStatus status,
            @Param("now") LocalDateTime now);

}
