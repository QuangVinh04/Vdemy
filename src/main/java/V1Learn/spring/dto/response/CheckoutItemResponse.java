package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutItemResponse {
    String courseId;
    String courseName;
    String checkoutId;
    BigDecimal price;
}
