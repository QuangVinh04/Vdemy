package V1Learn.spring.DTO.Request;


import V1Learn.spring.utils.CourseLevel;
import V1Learn.spring.utils.CourseStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreationRequest {

    String title;
    String description;
    String language;
    Long price;
    CourseLevel level;
    String duration;
    CourseStatus status;
    List<ChapterRequest> chapters;

}
