package V1Learn.spring.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonProgress extends AbstractEntity {


    @Column(name = "is_completed")
    Boolean isCompleted = false;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "last_watched_at")
    Integer lastWatchedAt = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

}
