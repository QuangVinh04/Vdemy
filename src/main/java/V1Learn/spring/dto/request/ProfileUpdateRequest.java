package V1Learn.spring.dto.request;

import V1Learn.spring.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {
    String id;
    String firstName;
    String lastName;
    Gender gender;
    String phone;
    String address;
    LocalDate dob;
    // media
    String avatarUrl;
    String avatarPublicId;
}
