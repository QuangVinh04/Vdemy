package V1Learn.spring.repository;



import V1Learn.spring.entity.Category;
import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findFirstByOrderByCreatedAtAsc();


    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.id <> :id")
    Category findExistedName(@Param("name") String name, @Param("id") String id);

    
}

