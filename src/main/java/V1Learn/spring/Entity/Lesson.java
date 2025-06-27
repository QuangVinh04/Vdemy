package V1Learn.spring.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@Table(name = "lesson")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends AbstractEntity {

    @Column(name = "name")
    String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    Chapter chapter;

    @Column(name = "content_type")
    String contentType; // "video", "document", etc.

    @Column(name = "content_url")
    String contentUrl;

    @Column(name = "description")
    String description; // Mô tả nội dung nếu cần

    @Column(name = "order_index")
    Integer orderIndex;

}
