package V1Learn.spring.Repostiory;

import V1Learn.spring.DTO.Response.ChapterResponse;
import V1Learn.spring.Entity.Chapter;
import V1Learn.spring.projection.ChapterSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

    @Query("SELECT MAX(c.orderIndex) FROM Chapter c WHERE c.course.id = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(String courseId);


    List<Chapter> findByCourseId(String courseId);


    @Query("""
        SELECT new V1Learn.spring.projection.ChapterSummaryProjection(
          ch.id,
          ch.title,
          ch.description,
          ch.orderIndex,
          COUNT(l.id),
          SUM(l.videoDuration)
            )
            FROM Chapter ch
            LEFT JOIN ch.lessons l
            WHERE ch.course.id = :courseId
            GROUP BY ch.id, ch.title, ch.description, ch.orderIndex
            ORDER BY ch.orderIndex
      """)
    List<ChapterSummaryProjection> findChapterSummariesByCourseId(@Param("courseId") String courseId);
}

