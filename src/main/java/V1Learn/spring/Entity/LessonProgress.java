package V1Learn.spring.Entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonProgress extends AbstractEntity {


    @Column(name = "is_completed")
    Boolean isCompleted = false;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "watch_time")
    Integer watchTime = 0; // in seconds

    @Column(name = "last_watched_at")
    LocalDateTime lastWatchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

}
