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
public class CategoryUpdateRequest {

    String name;

    String description;

    @NotBlank
    Boolean isActive; // Cho phép admin ẩn danh mục này đi thay vì xóa vĩnh viễn
}
