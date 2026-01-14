package V1Learn.spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem extends AbstractEntity {

    @Column(precision = 10, scale = 2, nullable = false)
    BigDecimal price;

    @Column(name = "discount_price", precision = 10, scale = 2)
    BigDecimal discountPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @JoinColumn(name = "course_id", nullable = false)
    String courseId;
}
