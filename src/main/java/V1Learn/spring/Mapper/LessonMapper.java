package V1Learn.spring.Mapper;


import V1Learn.spring.DTO.Request.LessonRequest;
import V1Learn.spring.DTO.Response.LessonResponse;
import V1Learn.spring.Entity.Lesson;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toLesson(LessonRequest lessonRequest);

    LessonResponse toLessonResponse(Lesson lesson);


}
