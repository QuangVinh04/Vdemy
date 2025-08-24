package V1Learn.spring.Entity;

import V1Learn.spring.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends AbstractEntity {


    @Column(name = "order_code", unique = true, nullable = false)
    String orderCode;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", precision = 10, scale = 2, nullable = false)
    BigDecimal finalAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(name = "payment_transaction_id")
    String paymentTransactionId;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    LocalDateTime cancelledAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Payment> payments;

    @PrePersist
    protected void onCreate() {
        if (orderCode == null) {
            orderCode = "ORD" + System.currentTimeMillis();
        }
    }
}
