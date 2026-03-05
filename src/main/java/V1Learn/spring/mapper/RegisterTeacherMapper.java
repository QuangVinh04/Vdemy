package V1Learn.spring.mapper;

import V1Learn.spring.dto.request.RegisterTeacherRequest;
import V1Learn.spring.dto.response.RegisterTeacherResponse;
import V1Learn.spring.entity.RegisterTeacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterTeacherMapper {

    RegisterTeacher toRegisterTeacher(RegisterTeacherRequest request);

    RegisterTeacherResponse toTeacherResponse(RegisterTeacher application);

}
