package V1Learn.spring.dto.response;


import V1Learn.spring.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEnrolledResponse {
    String id;
    String firstName;
    String lastName;
    String phone;
    String email;
    LocalDate dob;
    Gender gender;
    String address;
    String avatar;
    LocalDateTime enrolledOn;

    public UserEnrolledResponse(String id, String firstName, String lastName, String phone, String email, LocalDate dob, Gender gender, String address, String avatar,  LocalDateTime enrolledOn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.avatar = avatar;
        this.enrolledOn = enrolledOn;
    }
}
