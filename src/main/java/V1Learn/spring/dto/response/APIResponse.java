package V1Learn.spring.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // giá trị nào = null thì ko trả về
public class APIResponse <T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;
}
