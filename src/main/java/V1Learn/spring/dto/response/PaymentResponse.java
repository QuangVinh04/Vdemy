package V1Learn.spring.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import V1Learn.spring.enums.PaymentMethod;
import V1Learn.spring.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String id;
    String orderCode;
    String message;
    String paymentId;
    String paymentUrl;
    PaymentMethod paymentMethod;
    BigDecimal amount;
    LocalDateTime createdAt;
    PaymentStatus status;
}
