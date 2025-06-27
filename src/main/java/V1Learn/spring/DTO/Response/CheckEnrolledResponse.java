package V1Learn.spring.DTO.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckEnrolledResponse {
    String currentUserId;
    boolean valid;
}
