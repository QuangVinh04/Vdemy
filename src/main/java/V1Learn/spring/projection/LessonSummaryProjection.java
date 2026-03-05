package V1Learn.spring.projection;

import V1Learn.spring.enums.LessonType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonSummaryProjection {
    String id;
    String name;
    String description;
    String videoUrl;
    String fileUrl;
    Long videoDuration;
    Integer orderIndex;
    LessonType type;
    Boolean isPreview;
    Boolean isPublished;
    String chapterId;
}