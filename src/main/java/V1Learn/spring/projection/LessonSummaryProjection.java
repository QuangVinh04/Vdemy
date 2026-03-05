package V1Learn.spring.projection;

import V1Learn.spring.enums.LessonType;


import java.io.Serializable;


public record LessonSummaryProjection (
    String id,
    String name,
    String description,
    String videoUrl,
    String fileUrl,
    Long videoDuration,
    Integer orderIndex,
    LessonType type,
    Boolean isPreview,
    Boolean isPublished,
    String chapterId
) implements Serializable {}