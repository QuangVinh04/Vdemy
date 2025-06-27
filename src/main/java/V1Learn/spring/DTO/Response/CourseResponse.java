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
public class CourseResponse {
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
    Set<ChapterResponse> chapters;
}
