package V1Learn.spring.repository;


import V1Learn.spring.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    void deleteByCartUserIdAndCourseId(String userId, String courseId);
}

