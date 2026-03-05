package V1Learn.spring.mapper;

import V1Learn.spring.dto.request.ProfileUpdateRequest;
import V1Learn.spring.dto.response.ProfileResponse;
import V1Learn.spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponse toProfileResponse(User user);

    void updateUserProfile (@MappingTarget User user, ProfileUpdateRequest profileUpdateRequest);


}
