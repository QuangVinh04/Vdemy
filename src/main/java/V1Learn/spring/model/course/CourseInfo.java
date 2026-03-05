package V1Learn.spring.model.course;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseInfo {
    String id;
    String name;
    BigDecimal price;
}
