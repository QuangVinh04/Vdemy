package V1Learn.spring.Mapper;


import V1Learn.spring.DTO.Request.CourseUpdateRequest;
import V1Learn.spring.DTO.Request.LessonRequest;
import V1Learn.spring.DTO.Response.LessonResponse;
import V1Learn.spring.Entity.Course;
import V1Learn.spring.Entity.Lesson;
import V1Learn.spring.projection.LessonSummaryProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toLesson(LessonRequest lessonRequest);


    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    LessonResponse toLessonResponse(LessonSummaryProjection lesson);

    LessonResponse toLessonResponseBase(Lesson lesson);


    void updateLesson(@MappingTarget Lesson lesson, LessonRequest request);

}
