package V1Learn.spring.repository;



import V1Learn.spring.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {

    @Query("SELECT a.lesson.id FROM LessonProgress a " +
            "WHERE a.enrollment.user.id = :userId AND a.enrollment.course.id = :courseId " +
            "AND a.isCompleted = true")
    Set<String> findCompletedLessonIdsByUserAndCourse(@Param("userId") String userId,
                                                      @Param("courseId") String courseId);

    long countByEnrollmentIdAndIsCompletedTrue(String enrollmentId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "JOIN FETCH lp.lesson l " +
            "WHERE lp.enrollment.id = :enrollmentId")
    List<LessonProgress> findByEnrollmentId (@Param("enrollmentId") String enrollmentId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "JOIN FETCH lp.enrollment e " +
            "JOIN FETCH e.user u " +
            "JOIN FETCH lp.lesson l " +
            "JOIN FETCH l.chapter c " +
            "JOIN FETCH c.course co " +
            "WHERE e.id = :enrollmentId AND l.id = :lessonId")
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(
            @Param("enrollmentId") String enrollmentId,
            @Param("lessonId") String lessonId);
}

