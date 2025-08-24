package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


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
