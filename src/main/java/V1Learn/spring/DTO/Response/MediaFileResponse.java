package V1Learn.spring.DTO.Response;


import V1Learn.spring.utils.CourseLevel;
import V1Learn.spring.utils.CourseStatus;
import V1Learn.spring.utils.ResourceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaFileResponse {
    String id;
    String publicId;
    String secureUrl;
    String originalFilename;
    ResourceType resourceType;
    String format;
    Long bytes;
    Integer width;
    Integer height;
    Integer duration;
    String folder;
    String title;
    String caption;
    String description;
    String uploadedBy;
    Boolean isPublic;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
