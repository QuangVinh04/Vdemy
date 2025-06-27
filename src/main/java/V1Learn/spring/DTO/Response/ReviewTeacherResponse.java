package V1Learn.spring.DTO.Response;


import V1Learn.spring.utils.CourseLevel;
import V1Learn.spring.utils.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


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
    String createdAT;
}
