package V1Learn.spring.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {
    @NotBlank(message = "CATEGORY_NAME_REQUIRED") // Key để map vào message properties sau này
    @Size(min = 3, message = "CATEGORY_NAME_MIN_3_CHARS")
    String name;

    String description;
}
