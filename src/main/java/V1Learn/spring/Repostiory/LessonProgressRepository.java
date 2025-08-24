package V1Learn.spring.Repostiory;



import V1Learn.spring.Entity.CourseAccess;
import V1Learn.spring.Entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {

    @Query("SELECT a FROM LessonProgress a " +
            "WHERE a.enrollment.user.id = :userId AND a.enrollment.course.id = :courseId " +
            "AND a.isCompleted = true")
    Set<String> findCompletedLessonIdsByUserAndCourse(@Param("userId") String userId,
                                                      @Param("courseId") String courseId);
}

