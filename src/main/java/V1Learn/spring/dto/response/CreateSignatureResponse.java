package V1Learn.spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;


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
