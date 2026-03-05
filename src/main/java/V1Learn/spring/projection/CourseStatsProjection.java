package V1Learn.spring.projection;



import java.io.Serializable;


public record CourseStatsProjection(
    Long totalStudents,
    Double rating,
    Long totalReviews
) implements Serializable {}