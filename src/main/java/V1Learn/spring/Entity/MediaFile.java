package V1Learn.spring.Entity;

import V1Learn.spring.utils.ResourceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Table(name = "media_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaFile extends AbstractEntity {

    @Column(name = "public_id", nullable = false, unique = true)
    String publicId;

    @Column(name = "secure_url", nullable = false)
    String secureUrl;

    @Column(name = "original_filename")
    String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    ResourceType resourceType;

    @Column(name = "format")
    String format;

    @Column(name = "bytes")
    Long bytes;

    @Column(name = "width")
    Integer width;

    @Column(name = "height")
    Integer height;

    @Column(name = "duration")
    Integer duration; // seconds for video

    @Column(name = "folder")
    String folder;

    @Column(name = "title")
    String title;

    @Column(name = "caption")
    String caption;

    @Column(name = "description")
    String description;

    @Column(name = "uploaded_by")
    String uploadedBy; // user ID

    @Column(name = "is_public")
    Boolean isPublic = false;

}
