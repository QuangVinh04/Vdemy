package V1Learn.spring.DTO.Response.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminTeacherResponse {
    String id;
    String lastName;
    String email;
    String gender;
    String status;
    Set<String> roles;
    LocalDateTime createdAT;
}


