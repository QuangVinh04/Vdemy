package V1Learn.spring.Mapper;

import V1Learn.spring.DTO.Request.UserCreationRequest;
import V1Learn.spring.DTO.Response.UserResponse;
import V1Learn.spring.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest request);

    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);


}
