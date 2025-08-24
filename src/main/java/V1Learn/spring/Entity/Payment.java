package V1Learn.spring.Entity;


import V1Learn.spring.enums.PaymentMethod;
import V1Learn.spring.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment extends AbstractEntity {

    @Column(name = "transaction_id", unique = true, nullable = false)
    String transactionId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    BigDecimal amount;

    @Column(name = "currency")
    String currency = "VND";

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(name = "gateway_transaction_id")
     String gatewayTransactionId;

    @Column(name = "description")
    String description;

    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    PaymentStatus status = PaymentStatus.PENDING;
    
    @PrePersist
    protected void onCreate() {
        if (transactionId == null) {
            transactionId = "PAY" + System.currentTimeMillis();
        }
        // Set expiration time (30 minutes from creation)
        expiredAt = getCreatedAt().plusMinutes(30);
    }
}
