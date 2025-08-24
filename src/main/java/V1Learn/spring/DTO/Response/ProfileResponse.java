package V1Learn.spring.DTO.Response;


import V1Learn.spring.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {
    String avatar;
    String firstName;
    String lastName;
    String phone;
    String email;
    String address;
    LocalDate dob;
    Gender gender;

}
