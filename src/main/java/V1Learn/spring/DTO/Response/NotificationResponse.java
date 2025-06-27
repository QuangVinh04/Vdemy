package V1Learn.spring.DTO.Response;


import V1Learn.spring.utils.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    String title;
    String message;
    Boolean isRead;
    String url;
    LocalDateTime timestamp;
}
