package V1Learn.spring.Repostiory;


import V1Learn.spring.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByRecipientIdAndIsReadFalse(String userId);

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId ORDER BY n.createdAT DESC")
    List<Notification> findByRecipientIdOrderByIdDesc(String userId);

}

