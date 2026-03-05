package V1Learn.spring.repository;



import V1Learn.spring.entity.CourseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface CourseAccessRepository extends JpaRepository<CourseAccess, String> {
    @Query("SELECT a FROM CourseAccess a " +
            "WHERE a.userId = :userId AND a.courseId = :courseId " +
            "AND a.isActive = true " +
            "AND (a.expiresAt IS NULL OR a.expiresAt > CURRENT_TIMESTAMP)")
    Optional<CourseAccess> findValidAccess(@Param("userId") String userId,
                                           @Param("courseId") String courseId);

    @Query("SELECT a.courseId FROM CourseAccess a " +
            "WHERE a.userId = :userId AND a.courseId IN :courseIds " +
            "AND a.isActive = true " +
            "AND (a.expiresAt IS NULL OR a.expiresAt > CURRENT_TIMESTAMP)")
    Set<String> findValidAccessCourseIds(@Param("userId") String userId,
                                         @Param("courseIds") Set<String> courseIds);
}

