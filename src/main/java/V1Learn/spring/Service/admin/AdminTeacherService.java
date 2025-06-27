package V1Learn.spring.Service.admin;


import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.admin.AdminTeacherResponse;
import V1Learn.spring.Entity.Role;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.admin.AdminTeacherMapper;

import V1Learn.spring.Repostiory.RoleRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.constant.PredefinedRole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service handles operations related to teacher management in the system.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTeacherService {

    UserRepository userRepository;
    AdminTeacherMapper teacherMapper;
    RoleRepository roleRepository;

    /**
     * Get list of teachers by page with pagination.
     *
     * @param pageable pagination object (page number, size, sort)
     * @return PageResponse object contains list of teachers and pagination information
     */
    public PageResponse<List<AdminTeacherResponse>> getAllTeachers(Pageable pageable) {
        Page<User> teacherPage = userRepository.findAllTeachers(pageable, PredefinedRole.TEACHER_ROLE);

        List<AdminTeacherResponse> teacherResponses = teacherPage.stream()
                .map(this::mapToTeacherResponse)
                .toList();

        return PageResponse.<List<AdminTeacherResponse>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(teacherPage.getTotalPages())
                .items(teacherResponses)
                .build();
    }

    /**
     * Converts a User object to an AdminTeacherResponse, including getting the user's roles.
     *
     * @param user the User object to convert
     * @return an AdminTeacherResponse object containing the teacher information
     */
    private AdminTeacherResponse mapToTeacherResponse(User user) {
        AdminTeacherResponse response = teacherMapper.toTeacherResponse(user);
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        response.setRoles(roles);
        return response;
    }

    /**
     * Removes the teacher role from the user, replacing it with the default user role.
     *
     * @param userId ID of the user whose teacher role needs to be removed
     * @throws AppException if the user or default role is not found
     */
    @Transactional
    public void removeTeacherRole(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role defaultUserRole = roleRepository.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS));

        // Reset roles to only USER_ROLE
        user.setRoles(Set.of(defaultUserRole));
        // Clear any teacher-specific registrations
        user.setRegisterTeachers(null);

        userRepository.save(user);
    }
}
