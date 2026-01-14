package V1Learn.spring.mapper;

import V1Learn.spring.dto.request.RoleRequest;
import V1Learn.spring.dto.response.RoleResponse;
import V1Learn.spring.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
