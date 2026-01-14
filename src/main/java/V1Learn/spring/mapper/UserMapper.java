package V1Learn.spring.mapper;

import V1Learn.spring.dto.request.UserCreationRequest;
import V1Learn.spring.dto.response.UserResponse;
import V1Learn.spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest request);

    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);


}
