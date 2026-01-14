package V1Learn.spring.repository;


import V1Learn.spring.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {

}

