package V1Learn.spring.dto.response;




import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    String name;
    String description;
    String videoUrl;
    String fileUrl;
    Integer videoDuration;
    Integer orderIndex;
    Boolean isPreview;
    Boolean isCompleted;
    Boolean isPublished;
    Boolean isLocked;
}
