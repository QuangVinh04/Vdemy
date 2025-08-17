package V1Learn.spring.DTO.Request;



import V1Learn.spring.utils.LessonType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRequest {
    String title;
    String description;
    LessonType lessonType;
    //media
    String videoPublicId;
    String videoUrl;
    Integer videoDuration;
    String filePublicId;
    String fileUrl;
}
