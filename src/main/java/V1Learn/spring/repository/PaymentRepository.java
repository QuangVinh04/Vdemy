package V1Learn.spring.repository;

import V1Learn.spring.entity.Payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Tìm payments theo userId thông qua Order relationship
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Payment> findByOrderUserIdOrderByCreatedAtDesc(@Param("userId") String userId, Pageable pageable);
//    @Query("SELECT p FROM Payment p " +
//            "WHERE p.enrollment.course.instructor.id = :userId " +
//            "AND p.createdAT BETWEEN :start AND :end " +
//            "AND p.status = :status")
//    List<Payment> findByUserAndDateRange(
//            @Param("userId") String userId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            @Param("status") PaymentStatus status
//    );

}
