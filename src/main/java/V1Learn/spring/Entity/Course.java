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
import java.util.List;
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

    @Column(name = "title")
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "language")
    String language;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    CourseLevel level;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "thumbnail_url")
    String thumbnailUrl;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "duration")
    String duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , length = 50)
    CourseStatus status;




}
