package V1Learn.spring.Mapper.admin;


import V1Learn.spring.DTO.Response.admin.AdminTeacherResponse;
import V1Learn.spring.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminTeacherMapper {
    @Mapping(source = "createdAT", target = "createdAT")
    @Mapping(target = "roles", ignore = true)
    AdminTeacherResponse toTeacherResponse(User user);
}


