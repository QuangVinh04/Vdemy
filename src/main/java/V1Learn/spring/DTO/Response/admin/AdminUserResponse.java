package V1Learn.spring.DTO.Response.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse {
    String id;
    String lastName;
    String email;
    String gender;
    String status;
    Set<String> roles;
    LocalDateTime createdAT;
}
