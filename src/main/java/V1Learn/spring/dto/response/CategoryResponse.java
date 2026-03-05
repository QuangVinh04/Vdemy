package V1Learn.spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    String id;
    String name;
    String description;
    Boolean isActive;

    // Các thông tin Audit
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
