package V1Learn.spring.DTO.Request;



import lombok.*;
import lombok.experimental.FieldDefaults;




@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreationRequest {

    String tempTitle;

}
