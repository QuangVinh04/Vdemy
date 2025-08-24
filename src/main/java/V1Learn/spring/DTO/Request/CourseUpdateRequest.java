package V1Learn.spring.DTO.Request;


import V1Learn.spring.enums.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {

    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    String title;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    BigDecimal price;

    @NotBlank(message = "Danh mục không được để trống")
    String categoryId;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    String language;

    @NotNull(message = "Cấp độ không được để trống")
    CourseLevel level;

    // media
    String thumbnailUrl;

    String thumbnailPublicId;

    String videoUrl;

    String videoPublicId;

    // additional
    String requirements;

    String whatYouLearn;

    String targetAudience;

    String materialsIncluded;
}
