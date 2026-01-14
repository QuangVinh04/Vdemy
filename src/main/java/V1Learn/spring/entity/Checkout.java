package V1Learn.spring.entity;


import V1Learn.spring.enums.CheckoutState;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@Entity
@Table(name = "check_out")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Checkout extends AbstractEntity  {

    @Column(name = "user_id", nullable = false)
    String userId;

    @Builder.Default
    BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    BigDecimal totalDiscountAmount = BigDecimal.ZERO;

    @Column(name = "payment_method_id")
    String paymentMethodId;

    @Column(name = "promotion_code")
    String promotionCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CheckoutState checkoutState;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<CheckoutItem> items = new HashSet<>();

}
