package V1Learn.spring.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "category")
public class Category extends AbstractEntity {

    @Column(nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "is_active")
    Boolean isActive = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    Set<Course> courses = new HashSet<>();

}
