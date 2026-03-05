package V1Learn.spring.repository;

import V1Learn.spring.entity.Checkout;
import V1Learn.spring.enums.CheckoutState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Checkout c WHERE c.id = :id")
    Optional<Checkout> findByIdWithLock(String id);

    List<Checkout> findByUserIdAndCheckoutState(String userId, CheckoutState state);

    // Scheduled Job: tìm checkout PENDING/PAYMENT_PROCESSING quá hạn → expire
    @Query("SELECT c FROM Checkout c WHERE c.checkoutState IN :states AND c.createdAt < :before")
    List<Checkout> findByCheckoutStateInAndCreatedAtBefore(
            @Param("states") List<CheckoutState> states,
            @Param("before") LocalDateTime before);

    // Scheduled Job: tìm checkout EXPIRED quá 7 ngày → xóa
    @Query("SELECT c FROM Checkout c WHERE c.checkoutState = :state AND c.updatedAt < :before")
    List<Checkout> findByCheckoutStateAndUpdatedAtBefore(
            @Param("state") CheckoutState state,
            @Param("before") LocalDateTime before);

}
