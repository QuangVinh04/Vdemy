package V1Learn.spring.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


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

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    Set<Lesson> lessons;

}
