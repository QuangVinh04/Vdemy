package V1Learn.spring.Mapper;

import V1Learn.spring.DTO.Request.CourseCreationRequest;

import V1Learn.spring.DTO.Request.CourseUpdateRequest;
import V1Learn.spring.DTO.Response.CourseResponse;
import V1Learn.spring.DTO.Response.CourseTeacherResponse;
import V1Learn.spring.Entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    Course toCourse(CourseCreationRequest request);




    @Mapping(target = "category", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    CourseResponse toCourseResponse(Course course);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    CourseTeacherResponse toCourseTeacherResponse(Course course);

    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateCourse(@MappingTarget Course course, CourseUpdateRequest request);

}
