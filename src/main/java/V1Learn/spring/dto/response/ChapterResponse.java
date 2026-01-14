package V1Learn.spring.dto.response;




import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    String id;
    String title;
    String description;
    Integer orderIndex;
    Long totalLessons;
    Long totalDuration;
    List<LessonResponse> lessons;
}
