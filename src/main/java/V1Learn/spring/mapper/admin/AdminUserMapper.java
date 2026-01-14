package V1Learn.spring.mapper.admin;


import V1Learn.spring.dto.response.admin.AdminUserResponse;
import V1Learn.spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(target = "roles", ignore = true)
    AdminUserResponse toUserResponse(User user);
}
