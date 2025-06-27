package V1Learn.spring.DTO.Request;

import V1Learn.spring.utils.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {
    String firstName;
    String lastName;
    Gender gender;
    String phone;
    String address;
    LocalDate dob;
}
