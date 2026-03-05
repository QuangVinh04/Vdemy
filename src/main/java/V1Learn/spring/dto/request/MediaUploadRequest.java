package V1Learn.spring.dto.request;



import V1Learn.spring.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Data
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaUploadRequest {

    @NotBlank(message = "Public ID is required")
    String publicId;

    @NotBlank(message = "Secure URL is required")
    String secureUrl;

    @NotBlank(message = "Original filename is required")
    String originalFilename;

    @NotNull(message = "Resource type is required")
    ResourceType resourceType;

    String format;
    Long bytes;
    Integer width;
    Integer height;
    Integer duration;        // seconds (video)
    String folder;
    String title;
    String caption;
    String description;

}
