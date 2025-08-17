package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.Payment;
import V1Learn.spring.Entity.User;
import V1Learn.spring.utils.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

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
