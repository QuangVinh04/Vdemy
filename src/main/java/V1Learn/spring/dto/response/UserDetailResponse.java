package V1Learn.spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse {
    String id;
    String fullName;
    String email;
    String phone;
    String gender;
    String avatar;
    LocalDate dob;               // Giữ nguyên kiểu LocalDate
    String description;
    String expertise;
    String certificateUrl;
    String portfolioUrl;
    Set<String> roles;
    LocalDateTime createdAt;
}
