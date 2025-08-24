package V1Learn.spring.DTO.Response;


import V1Learn.spring.enums.InstructorApplicationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterTeacherResponse {
    String id;
    String email;
    String name;
    String phone;
    String description;
    String expertise;
    String certificateUrl;
    String portfolioUrl;
    LocalDateTime createdAT;
    InstructorApplicationStatus status;
}
