package V1Learn.spring.repository;

import V1Learn.spring.entity.Course;

import V1Learn.spring.enums.CourseStatus;
import V1Learn.spring.projection.CourseStatsProjection;
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
            "LEFT JOIN FETCH c.instructor i " +
            "WHERE c.id = :courseId" )
    Optional<Course> findCourseBasicInfo(@Param("courseId") String courseId);

    Page<Course> findCourseByInstructorId(@Param("instructorId") String instructorId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.course.instructor.id = :userId " +
            "AND MONTH(r.createdAt) = :month " +
            "AND YEAR(r.createdAt) = :year")
    Double getAverageRatingByUserAndMonth(@Param("userId") String userId,
                                          @Param("month") int month,
                                          @Param("year") int year);

    @Query("SELECT c FROM Course c WHERE c.status = :status")
    Page<Course> findByStatus(Pageable pageable, CourseStatus status);

    @Query("SELECT c FROM Course c WHERE c.status = :status OR c.status = :status2")
    Page<Course> findByStatusEnabled(Pageable pageable, CourseStatus status, CourseStatus status2);


    boolean existsByThumbnailPublicId(String publicId);

    boolean existsByVideoPublicId(String publicId);

    @Query("SELECT COUNT(c) FROM Course c " +
            "WHERE c.instructor.id = :id")
    Integer countByInstructorId(@Param("id") String id);

    @Query("""
  SELECT new V1Learn.spring.projection.CourseStatsProjection(
    COUNT(DISTINCT e.id),
    AVG(r.rating),
    COUNT(r.id)
    )
  FROM Course c
  LEFT JOIN Enrollment e ON e.course.id = c.id
  LEFT JOIN Review r ON r.course.id = c.id
  WHERE c.id = :courseId
  GROUP BY c.id
  """)
    Optional<CourseStatsProjection> getCourseStats(@Param("courseId") String courseId);
}