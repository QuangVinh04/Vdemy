package V1Learn.spring.DTO.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEarningResponse {
    String orderId;
    LocalDate date;
    String course;
    BigDecimal amount;
}
