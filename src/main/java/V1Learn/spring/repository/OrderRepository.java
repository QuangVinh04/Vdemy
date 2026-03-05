package V1Learn.spring.repository;



import V1Learn.spring.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}

