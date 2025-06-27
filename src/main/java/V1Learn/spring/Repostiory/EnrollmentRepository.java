package V1Learn.spring.Repostiory;

import V1Learn.spring.DTO.Response.UserEnrolledResponse;
import V1Learn.spring.Entity.Course;
import V1Learn.spring.Entity.Enrollment;

import V1Learn.spring.Entity.User;
import V1Learn.spring.utils.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    List<Enrollment> findByUserAndCourseId(User user, String courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    @Query("SELECT e.course FROM Enrollment e WHERE e.user.id = :userId AND e.status = :status")
    Page<Course> findSuccessfulCoursesByUserId(@Param("userId") String userId,
                                               @Param("status") EnrollmentStatus status,
                                               Pageable pageable);
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status" )
    long countByCourseId(@Param("courseId") String courseId, @Param("status") EnrollmentStatus status);

    @Query("""
    SELECT new  V1Learn.spring.DTO.Response.UserEnrolledResponse(
        u.id, u.firstName, u.lastName, u.phone, u.email, u.dob, u.gender, u.address, u.avatar, e.createdAT
    )
    FROM Enrollment e
    JOIN e.user u
    WHERE e.course.instructor.id = :instructorId AND e.status = :status
    """)
    Page<UserEnrolledResponse> findAllByInstructorId(@Param("instructorId") String instructorId,
                                                     @Param("status") EnrollmentStatus status,
                                                     Pageable pageable);

    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.user.id = :userId AND e.course.instructor.id = :teacherId
    """)
    Optional<Enrollment> findByUserIdAndInstructorId(@Param("userId") String userId, @Param("teacherId") String teacherId);

}
