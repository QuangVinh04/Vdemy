package V1Learn.spring.model.payment;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitiatedPayment {
    String redirectUrl;
    LocalDateTime expiredAt;
}
