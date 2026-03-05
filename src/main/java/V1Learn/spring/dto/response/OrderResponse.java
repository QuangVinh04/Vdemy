package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    BigDecimal totalAmount;
    BigDecimal totalDiscountAmount;
    BigDecimal finalAmount;
    String paymentMethodId;
    Set<OrderItemResponse> items;
}
