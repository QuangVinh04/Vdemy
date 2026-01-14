package V1Learn.spring.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Getter
@Setter
@Builder
@Entity
@Table(name = "chapter")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chapter extends AbstractEntity  {

    @Column(name = "title", nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "order_index", nullable = false)
    Integer orderIndex;

//    @Column(name = "is_published")
//    Boolean isPublished = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    Course course;

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Lesson> lessons;

}
