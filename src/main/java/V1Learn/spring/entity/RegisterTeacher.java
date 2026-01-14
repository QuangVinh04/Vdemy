package V1Learn.spring.entity;


import V1Learn.spring.enums.InstructorApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@Entity
@Table(name = "register_teacher")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterTeacher extends AbstractEntity  {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "expertise", nullable = false)
    String expertise;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "certificate_url")
    String certificateUrl;

    @Column(name = "portfolio_url")
    String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255)
    InstructorApplicationStatus status;
}
