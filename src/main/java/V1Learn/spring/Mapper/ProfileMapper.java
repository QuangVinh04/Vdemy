package V1Learn.spring.Mapper;

import V1Learn.spring.DTO.Request.ProfileUpdateRequest;
import V1Learn.spring.DTO.Request.UserCreationRequest;
import V1Learn.spring.DTO.Response.ProfileResponse;
import V1Learn.spring.DTO.Response.UserResponse;
import V1Learn.spring.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponse toProfileResponse(User user);

    void updateUserProfile (@MappingTarget User user, ProfileUpdateRequest profileUpdateRequest);


}
