package V1Learn.spring.DTO.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSignatureResponse {
    String cloudName;
    String apiKey;
    String folder;
    String resourceType;
    Long timestamp;
    String signature;
}
