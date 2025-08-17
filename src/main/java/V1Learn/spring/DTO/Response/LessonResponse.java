package V1Learn.spring.DTO.Response;




import V1Learn.spring.utils.LessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    String title;
    String description;
    LessonType lessonType;
    String videoId;
    String videoUrl;
    String fileId;
    String fileUrl;
    Integer videoDuration;
    Integer orderIndex;
    Boolean isFree;
    Boolean isPublished;
}
