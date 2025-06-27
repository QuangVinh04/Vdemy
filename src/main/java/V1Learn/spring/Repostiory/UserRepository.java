package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.User;
import V1Learn.spring.constant.PredefinedRole;
import V1Learn.spring.utils.EnrollmentStatus;
import V1Learn.spring.utils.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(DISTINCT e.user.id) FROM Enrollment e " +
            "WHERE e.course.instructor.id = :userId " +
            "AND MONTH(e.createdAT) = :month " +
            "AND YEAR(e.createdAT) = :year " +
            "AND e.status = :status")
    int countNewStudentsByUserAndMonth(@Param("userId") String userId,
                                       @Param("month") int month,
                                       @Param("year") int year,
                                       EnrollmentStatus status);


    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    @Query("""
    SELECT u FROM User u
    JOIN u.roles r
    WHERE r.name = :roleName
    """)
    Page<User> findAllTeachers(Pageable pageable, String roleName);
}
