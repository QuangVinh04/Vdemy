package V1Learn.spring.Mapper.admin;


import V1Learn.spring.DTO.Response.admin.Admin_UserResponse;
import V1Learn.spring.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface Admin_UserMapper {

    @Mapping(target = "roles", ignore = true)
    Admin_UserResponse toUserResponse(User user);
}
