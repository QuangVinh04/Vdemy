package V1Learn.spring.Entity;


import V1Learn.spring.utils.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollment")
public class Enrollment extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    EnrollmentStatus status;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL)
    Payment payment;

}
