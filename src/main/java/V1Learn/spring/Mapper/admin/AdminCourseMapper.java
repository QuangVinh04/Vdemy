package V1Learn.spring.Mapper.admin;


import V1Learn.spring.DTO.Response.admin.AdminCourseResponse;
import V1Learn.spring.Entity.Course;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AdminCourseMapper {


    AdminCourseResponse toCourseResponse(Course course);
}
