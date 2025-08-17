package V1Learn.spring.Entity;


import V1Learn.spring.utils.CourseLevel;
import V1Learn.spring.utils.CourseStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "course")
public class Course extends AbstractEntity {

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "language")
    String language;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "discount_price")
    BigDecimal discountPrice;

    @Column(name = "thumbnail_url")
    String thumbnailUrl;

    @Column(name = "thumbnail_public_id")
    String thumbnailPublicId;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "video_public_id")
    String videoPublicId;

    @Column(name = "total_duration", nullable = false)
    Integer totalDuration = 2;

    @Column(name = "what_will_i_learn", columnDefinition = "TEXT")
    String whatWillILearn;

    @Column(name = "target_audience", columnDefinition = "TEXT")
    String targetAudience;

    @Column(name = "materials_included", columnDefinition = "TEXT")
    String materialsIncluded;

    @Column(name = "requirements_instructions", columnDefinition = "TEXT")
    String requirementsInstructions;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    @JsonIgnore
    User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("course")
    Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("course")
    Set<Review> comments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    Set<Chapter> chapters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , length = 50, nullable = false)
    CourseStatus status = CourseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    CourseLevel level;

    @Column(name = "published_at")
    LocalDateTime publishedAt;

    // Step completion tracking
    @Column(name = "step1_completed")
    Boolean step1Completed = false;

    @Column(name = "step2_completed")
    Boolean step2Completed = false;

    @Column(name = "step3_completed")
    Boolean step3Completed = false;
}
