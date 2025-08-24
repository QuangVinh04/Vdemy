package V1Learn.spring.Repostiory;



import V1Learn.spring.Entity.CourseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CourseAccessRepository extends JpaRepository<CourseAccess, String> {
    @Query("SELECT a FROM CourseAccess a " +
            "WHERE a.userId = :userId AND a.courseId = :courseId " +
            "AND (a.expiresAt IS NULL OR a.expiresAt > CURRENT_TIMESTAMP)")
    Optional<CourseAccess> findValidAccess(@Param("userId") String userId,
                                           @Param("courseId") String courseId);
}

