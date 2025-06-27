package V1Learn.spring.DTO.Request;

import V1Learn.spring.Entity.User;
import V1Learn.spring.Validator.DobConstraint;
import V1Learn.spring.utils.CourseLevel;
import V1Learn.spring.utils.CourseStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {

    String title;
    String description;
    String language;
    Long price;
    CourseLevel level;
    String duration;
    CourseStatus status;
    List<ChapterRequest> chapters;
}
