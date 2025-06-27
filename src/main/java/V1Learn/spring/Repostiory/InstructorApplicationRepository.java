package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.RegisterTeacher;

import V1Learn.spring.utils.InstructorApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorApplicationRepository extends JpaRepository<RegisterTeacher, String> {

    @Query("SELECT ia FROM RegisterTeacher ia WHERE  ia.status = :status ")
    Page<RegisterTeacher> findByOptionalStatus(@Param("status")InstructorApplicationStatus status, Pageable pageable);
}
