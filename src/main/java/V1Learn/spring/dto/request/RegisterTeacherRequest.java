package V1Learn.spring.dto.request;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterTeacherRequest {
    String expertise;
    String description;
    MultipartFile certificate;
    MultipartFile portfolio;
}
