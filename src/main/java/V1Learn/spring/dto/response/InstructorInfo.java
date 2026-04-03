package V1Learn.spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorInfo implements Serializable {
    String id;
    String fullName;
    String avatar;
    String bio;
    Long totalCourses;
    Long totalStudents;
}
