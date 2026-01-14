package V1Learn.spring.repository;

import V1Learn.spring.entity.RegisterTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisterTeacherRepository extends JpaRepository<RegisterTeacher, String> {


    Optional<RegisterTeacher> findByUserId(String userId);
}
