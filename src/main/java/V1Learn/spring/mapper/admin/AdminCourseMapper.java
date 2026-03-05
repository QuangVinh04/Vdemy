package V1Learn.spring.mapper.admin;


import V1Learn.spring.dto.response.admin.AdminCourseResponse;
import V1Learn.spring.entity.Course;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AdminCourseMapper {


    AdminCourseResponse toCourseResponse(Course course);
}
