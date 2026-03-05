package V1Learn.spring.repository;



import V1Learn.spring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findFirstByOrderByCreatedAtAsc();
}

