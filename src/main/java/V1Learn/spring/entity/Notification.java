package V1Learn.spring.entity;



import V1Learn.spring.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "notification")
public class Notification extends AbstractEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User recipient;

    @Column(name = "title")
    String title;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 255)
    NotificationType type;

    @Column(name = "is_read")
    Boolean isRead;

    @Column(name = "url")
    String url;

}
