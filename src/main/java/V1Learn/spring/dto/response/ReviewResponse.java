package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    String id;
    String userId;
    String content;
    Integer rating;
    String reviewerName;
    String reviewerAvatar;
    LocalDateTime createdAt;
    String instructorName;
    String reply; // phản hồi của giảng viên (nếu có)
    LocalDateTime replyAt;
}
