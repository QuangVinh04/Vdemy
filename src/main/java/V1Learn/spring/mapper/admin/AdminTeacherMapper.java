package V1Learn.spring.mapper.admin;


import V1Learn.spring.dto.response.admin.AdminTeacherResponse;
import V1Learn.spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminTeacherMapper {

    @Mapping(target = "roles", ignore = true)
    AdminTeacherResponse toTeacherResponse(User user);
}


