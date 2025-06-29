package V1Learn.spring.Mapper.admin;


import V1Learn.spring.DTO.Response.admin.AdminUserResponse;
import V1Learn.spring.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(target = "roles", ignore = true)
    AdminUserResponse toUserResponse(User user);
}
