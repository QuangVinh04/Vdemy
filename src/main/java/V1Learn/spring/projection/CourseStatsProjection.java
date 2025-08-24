package V1Learn.spring.projection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseStatsProjection {
    Long totalStudents;
    Double rating;
    Long totalReviews;
}