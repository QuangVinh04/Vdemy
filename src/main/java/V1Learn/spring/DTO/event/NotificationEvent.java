package V1Learn.spring.DTO.event;



import V1Learn.spring.utils.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String recipientId;
    String title;
    String content;
    String targetUrl;
    NotificationType type;
    LocalDateTime createdAt;
}
