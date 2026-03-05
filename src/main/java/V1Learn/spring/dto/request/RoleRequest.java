package V1Learn.spring.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    String name;
    String description;
    Set<String> permissions;
}
