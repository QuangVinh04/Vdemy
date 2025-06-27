package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.Course;

import V1Learn.spring.utils.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.chapters ch " +
            "LEFT JOIN FETCH ch.lessons " +
            "WHERE c.id = :courseId")
    Optional<Course> findCourseWithChaptersAndLessons(@Param("courseId") String courseId);

    Page<Course> findCourseByInstructorId(@Param("instructorId") String instructorId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.course.instructor.id = :userId " +
            "AND MONTH(r.createdAT) = :month " +
            "AND YEAR(r.createdAT) = :year")
    Double getAverageRatingByUserAndMonth(@Param("userId") String userId,
                                          @Param("month") int month,
                                          @Param("year") int year);

    @Query("SELECT c FROM Course c WHERE c.status = :status")
    Page<Course> findByStatus(Pageable pageable, CourseStatus status);

    @Query("SELECT c FROM Course c WHERE c.status = :status OR c.status = :status2")
    Page<Course> findByStatusEnabled(Pageable pageable, CourseStatus status, CourseStatus status2);
}

