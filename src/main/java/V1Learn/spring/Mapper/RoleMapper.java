package V1Learn.spring.Mapper;

import V1Learn.spring.DTO.Request.RoleRequest;
import V1Learn.spring.DTO.Response.RoleResponse;
import V1Learn.spring.Entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
