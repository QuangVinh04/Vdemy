package V1Learn.spring.DTO.Response;


import V1Learn.spring.utils.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

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
