package V1Learn.spring.projection;


import java.io.Serializable;

public record ChapterSummaryProjection (
    String id,
    String title,
    String description,
    Integer orderIndex,
    Long totalLessons,          // COUNT → Long
    Long totalDurationSeconds  // SUM → Long
) implements Serializable {}