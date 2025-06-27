package V1Learn.spring.DTO.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;


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
    LocalDateTime createdAT;
    String instructorName;
    String reply; // phản hồi của giảng viên (nếu có)
    LocalDateTime replyAt;
}
