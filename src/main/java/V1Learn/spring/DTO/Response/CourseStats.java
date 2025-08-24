package V1Learn.spring.DTO.Response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
