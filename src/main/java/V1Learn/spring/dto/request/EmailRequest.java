package V1Learn.spring.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    String email;
}
