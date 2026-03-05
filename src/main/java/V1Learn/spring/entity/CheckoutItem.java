package V1Learn.spring.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@Entity
@Table(name = "check_out_item")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutItem extends AbstractEntity  {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", nullable = false, updatable = false)
    @JsonBackReference
    Checkout checkout;

    @Column(name = "course_id", nullable = false, updatable = false)
    String courseId;


    @Column(name = "price")
    BigDecimal price;

    @Column(name = "discount_price")
    BigDecimal discountPrice;


}
