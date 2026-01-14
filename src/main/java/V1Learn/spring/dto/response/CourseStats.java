package V1Learn.spring.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseStats {
    Long totalStudents;
    Double rating;
    Long totalReviews;
    Long totalChapters;
    Long totalLessons;
    Long totalDuration;
}
