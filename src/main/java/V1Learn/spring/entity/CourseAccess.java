package V1Learn.spring.entity;


import V1Learn.spring.enums.AccessType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_access")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseAccess  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    String id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "course_id")
    String courseId;

    @Enumerated(EnumType.STRING)
    AccessType accessType;

    @Column(name = "granted_at")
    LocalDateTime grantedAt;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    @Column(name = "active")
    boolean isActive = false;
}
