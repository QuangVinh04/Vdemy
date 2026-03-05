package V1Learn.spring.dto.response;


import V1Learn.spring.enums.CourseLevel;
import V1Learn.spring.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailResponse {
    String id;
    String title;
    String description;
    String language;
    Long price;
    CourseLevel level;
    String instructorName;
    String instructorAvatar;
    String duration;
    String thumbnailUrl;
    String videoUrl;
    CourseStatus status;
    String category;
    InstructorInfo instructor;
    CourseStats stats;
    List<ChapterResponse> chapters;
    AccessInfo accessInfo;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
