package V1Learn.spring.entity;


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
@Entity
@Table(name = "payment")
public class Payment extends AbstractEntity {

    @Column(name = "transaction_id", unique = true, nullable = false)
    String transactionId;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal amount;

    @Column(nullable = false)
    String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;  // vnpay/paypal

    @Column(name = "gateway_transaction_id")
    String gatewayTransactionId;

    @Column(name = "failure_reason")
    String failureReason;

    @Column(name = "gateway_raw_response", columnDefinition = "TEXT")
    String gatewayRawResponse;

    @Column(name = "description")
    String description;

    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status = PaymentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (transactionId == null) {
            transactionId = "PAY" + System.currentTimeMillis();
        }

        this.paymentDate = now;
        this.expiredAt = now.plusMinutes(30);
    }
}

