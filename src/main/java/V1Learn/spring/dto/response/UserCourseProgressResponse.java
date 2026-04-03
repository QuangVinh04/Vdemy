package V1Learn.spring.dto.response;

import V1Learn.spring.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCourseProgressResponse {
    Double overallPercentage;      // % hoàn thành cả khóa
    Integer lastWatchedAt;     // Giây cuối cùng của bài học đó
    Set<String> completedLessonIds; // Danh sách ID đã xong (để hiện dấu tick)
    Map<String, Integer> lessonWatchHistory; // Map<LessonId, Second> cho các bài đang học dở
}
