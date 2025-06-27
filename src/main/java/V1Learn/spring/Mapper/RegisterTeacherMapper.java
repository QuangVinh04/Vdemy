package V1Learn.spring.Mapper;

import V1Learn.spring.DTO.Request.RegisterTeacherRequest;
import V1Learn.spring.DTO.Response.RegisterTeacherResponse;
import V1Learn.spring.Entity.RegisterTeacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterTeacherMapper {

    RegisterTeacher toRegisterTeacher(RegisterTeacherRequest request);

    RegisterTeacherResponse toTeacherResponse(RegisterTeacher application);

}
