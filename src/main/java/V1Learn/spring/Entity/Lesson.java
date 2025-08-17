package V1Learn.spring.Entity;


import V1Learn.spring.utils.LessonType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@Entity
@Table(name = "lesson")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends AbstractEntity {

    @Column(name = "title", nullable = false)
    String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    LessonType lessonType;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "video_public_id")
    String videoPublicId;

    @Column(name = "file_url")
    String fileUrl;

    @Column(name = "file_public_id")
    String filePublicId;

    @Column(name = "video_duration")
    Integer videoDuration;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "order_index")
    Integer orderIndex;

    @Column(name = "is_free")
    Boolean isFree = false;

    @Column(name = "is_published")
    Boolean isPublished = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    Chapter chapter;
}
