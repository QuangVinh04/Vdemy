package V1Learn.spring.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCourseResponse {
    String id;
    String authorId; // Thêm trường này vào DTO
    String title;
    String description;
    String status;
    String authorName;
    String language;
    String level;
    String duration;
    Boolean enabled;
    BigDecimal price;
    String thumbnailUrl;
    LocalDateTime createdAT; // Định dạng đúng kiểu dữ liệu
    LocalDateTime updatedAT;
}