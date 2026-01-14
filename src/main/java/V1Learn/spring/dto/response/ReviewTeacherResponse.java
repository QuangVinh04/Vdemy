package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewTeacherResponse {
    String id;
    String courseId;
    String courseTitle;
    String reviewerName;
    String reviewerAvatar;
    String content;
    Integer rating;
    String reply;
    String createdAt;
}
