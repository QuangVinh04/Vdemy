package V1Learn.spring.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorInfo {
    String id;
    String fullName;
    String avatar;
    String bio;
    Integer totalCourses;
    Integer totalStudents;
}
