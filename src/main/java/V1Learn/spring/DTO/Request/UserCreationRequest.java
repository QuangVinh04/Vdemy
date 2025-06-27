package V1Learn.spring.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;

    @NotBlank(message = "FirstName cannot be blank")
    String firstName;

    @NotBlank(message = "LastName cannot be blank")
    String lastName;

    @NotBlank(message = "Phone cannot be blank")
    String phone;


    LocalDate dob;

}
