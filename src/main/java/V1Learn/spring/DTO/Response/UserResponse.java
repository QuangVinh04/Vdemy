package V1Learn.spring.DTO.Response;


import V1Learn.spring.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String firstName;
    String lastName;
    String phone;
    String email;
    LocalDate dob;
    Gender gender;
    String address;
    String avatar;
    Set<String> roles;

}
