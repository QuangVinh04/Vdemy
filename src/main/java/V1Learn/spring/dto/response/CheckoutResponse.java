package V1Learn.spring.dto.response;


import V1Learn.spring.enums.CheckoutState;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutResponse {
    String id;
    String promotionCode;
    CheckoutState checkoutState;
    BigDecimal totalAmount;
    BigDecimal totalDiscountAmount;
    String paymentMethodId;
    Set<CheckoutItemResponse> items;
}
