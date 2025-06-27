package V1Learn.spring.Repostiory;


import V1Learn.spring.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {


    Page<Review> findAllByCourseId(String courseId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.course.instructor.id = :instructorId")
    Page<Review> findByInstructorId(@Param("instructorId") String instructorId, Pageable pageable);
}

