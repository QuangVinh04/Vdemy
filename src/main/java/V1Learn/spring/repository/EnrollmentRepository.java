package V1Learn.spring.repository;

import V1Learn.spring.entity.Course;
import V1Learn.spring.entity.Enrollment;

import V1Learn.spring.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

        List<Enrollment> findByStatus(EnrollmentStatus status);

        @Query("SELECT e.course FROM Enrollment e WHERE e.user.id = :userId AND e.status = :status")
        Page<Course> findSuccessfulCoursesByUserId(@Param("userId") String userId,
                        @Param("status") EnrollmentStatus status,
                        Pageable pageable);

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
        long countByCourseId(@Param("courseId") String courseId, @Param("status") EnrollmentStatus status);

        Optional<Enrollment> findByUserIdAndCourseId(String userId, String courseId);

        @Query("SELECT e FROM Enrollment e " +
                        "JOIN FETCH e.course c " +
                        "WHERE e.id = :enrollmentId")
        Optional<Enrollment> findByIdWithCourse(@Param("enrollmentId") String enrollmentId);

        @Query("SELECT COUNT(e) FROM Enrollment e " +
                        "WHERE e.course.instructor.id = :id")
        long countStudentsByInstructorId(@Param("id") String id);

        boolean existsByUserIdAndCourseId(String userId, String courseId);

        Page<Enrollment> findByUserId(String userId, Pageable pageable);

}
