package V1Learn.spring.Repostiory;


import V1Learn.spring.Entity.Lesson;
import V1Learn.spring.projection.LessonSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    @Query("SELECT MAX(c.orderIndex) FROM Lesson c WHERE c.chapter.id = :chapterId")
    Optional<Integer> findMaxOrderIndexByChapterId(String chapterId);


    boolean existsByVideoPublicId(String publicId);

    @Query("""
      SELECT new V1Learn.spring.projection.LessonSummaryProjection(
          l.id,
          l.name,
          l.description,
          l.videoUrl,
          l.fileUrl,
          l.videoDuration,
          l.orderIndex,
          l.lessonType,
          l.isPreview,
          l.isPublished,
          l.chapter.id
      )
      FROM Lesson l
      WHERE l.chapter.id IN :chapterIds
      ORDER BY l.chapter.orderIndex, l.orderIndex
      """)
    List<LessonSummaryProjection> findLessonsByChapterIds(@Param("chapterIds") List<String> chapterIds);
}

