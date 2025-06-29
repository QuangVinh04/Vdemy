package V1Learn.spring.Service.admin;

import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.UserDetailResponse;
import V1Learn.spring.DTO.Response.admin.AdminUserResponse;
import V1Learn.spring.Entity.RegisterTeacher;
import V1Learn.spring.Entity.Role;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.admin.AdminUserMapper;
import V1Learn.spring.Repostiory.RegisterTeacherRepository;
import V1Learn.spring.Repostiory.RoleRepository;
import V1Learn.spring.Repostiory.UserRepository;

import V1Learn.spring.utils.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Service provides user management functions for admin.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserService {

    UserRepository userRepository;
    AdminUserMapper userMapper;
    RoleRepository roleRepository;
    RegisterTeacherRepository registerTeacherRepository;

    /**
     * Get the list of paged users.
     *
     * @param pageable the paged information
     * @return PageResponse containing the list of users
     */
    public PageResponse<List<AdminUserResponse>> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        List<AdminUserResponse> userResponses = users.stream()
                .map(u -> {
                    AdminUserResponse response = userMapper.toUserResponse(u);
                    Set<String> roles = u.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    response.setRoles(roles);
                    return response;
                }).toList();

        return PageResponse.<List<AdminUserResponse>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(userResponses)
                .build();
    }


    /**
     * Disable the user account.
     *
     * @param userId ID of the user to ban
     * @throws AppException if the user does not exist or has been banned
     */
    @Transactional
    public void banUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus()!= null && user.getStatus().equals(UserStatus.NONE)) {
            throw new AppException(ErrorCode.USER_ALREADY_BANNED);
        }

        user.setStatus(UserStatus.NONE); // Vô hiệu hóa tài khoản
        userRepository.save(user);
    }

    /**
     * Reactivate a banned user account.
     *
     * @param userId ID of the user to unban
     * @throws AppException if the user does not exist or has not been banned
     */
    @Transactional
    public void unbanUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != null && user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new AppException(ErrorCode.USER_NOT_BANNED);
        }

        user.setStatus(UserStatus.ACTIVE); // Kích hoạt lại tài khoản
        userRepository.save(user);
    }

    /**
     * Updates the user's role.
     *
     * @param userId The user ID
     * @param roleName The new role name
     * @throws AppException if the user or role does not exist
     */
    @Transactional
    public void updateUserRole(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role newRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS));

        Set<Role> roles = new HashSet<>();
        roles.add(newRole);

        user.setRoles(roles); // Update user role
        userRepository.save(user);
    }


    /**
     * Get the user's teacher application details.
     *
     * @param userId The user ID
     * @return UserDetailResponse containing the details
     * @throws AppException if the user does not exist
     */
    @Transactional(readOnly = true)
    public UserDetailResponse getUserApplicationDetail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        RegisterTeacher registerTeacher = registerTeacherRepository.findByUserId(userId)
                .orElse(null);
        if (registerTeacher != null) {
            return UserDetailResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .gender(user.getGender() != null ? user.getGender().toString() : null) // Kiểm tra null
                    .avatar(user.getAvatar())
                    .dob(user.getDob()) // Giữ nguyên LocalDate
                    .description(registerTeacher.getDescription())
                    .expertise(registerTeacher.getExpertise())
                    .certificateUrl(registerTeacher.getCertificateUrl())
                    .portfolioUrl(registerTeacher.getPortfolioUrl())
                    .createdAT(user.getCreatedAT())
                    .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                    .build();
        }

        return UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender() != null ? user.getGender().toString() : null) // Kiểm tra null
                .avatar(user.getAvatar())
                .dob(user.getDob()) // Giữ nguyên LocalDate
                .description("")
                .expertise("")
                .certificateUrl("")
                .portfolioUrl("")
                .createdAT(user.getCreatedAT())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }


}

