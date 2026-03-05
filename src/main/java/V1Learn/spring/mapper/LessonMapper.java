package V1Learn.spring.mapper;


import V1Learn.spring.dto.request.LessonRequest;
import V1Learn.spring.dto.response.LessonResponse;
import V1Learn.spring.entity.Lesson;
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
