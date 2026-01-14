package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardSummaryResponse {
    BigDecimal revenueThisMonth;
    Double ratingThisMonth;
    Integer newStudentsThisMonth;
}
