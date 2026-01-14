package V1Learn.spring.entity;


import V1Learn.spring.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollment")
public class Enrollment extends AbstractEntity {


    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "progress_percentage")
    Double progressPercentage = 0.0;

    @Column(name = "last_accessed_at")
    LocalDateTime lastAccessedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL)
    Set<LessonProgress> lessonProgresses;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    EnrollmentStatus status;
}
