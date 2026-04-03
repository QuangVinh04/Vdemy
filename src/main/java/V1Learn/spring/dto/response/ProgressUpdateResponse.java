package V1Learn.spring.dto.response;

import V1Learn.spring.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressUpdateResponse {
    String enrollmentId;
    Double progressPercentage;
    EnrollmentStatus courseStatus;
    String nextLessonId;
    boolean isLessonCompleted;
    LocalDateTime updatedAt;
}
