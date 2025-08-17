package V1Learn.spring.controller.admin;


import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.UserDetailResponse;
import V1Learn.spring.DTO.Response.admin.AdminUserResponse;
import V1Learn.spring.Service.admin.AdminUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    AdminUserService adminUserService;

    @GetMapping
    public APIResponse<PageResponse<List<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {
        log.info("Get all users by admin");
        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        return APIResponse.<PageResponse<List<AdminUserResponse>>>builder()
                .message("Get all users successfully")
                .result(adminUserService.getAllUsers(pageable))
                .build();
    }


    @PostMapping("/ban/{userId}")
    public APIResponse<String> banUser(@PathVariable String userId) {
        log.info("Ban user: {}", userId);
        adminUserService.banUser(userId);
        return APIResponse.<String>builder()
                .message("Ban user successfully")
                .build();
    }

    @PostMapping("/unban/{userId}")
    public APIResponse<String> unbanUser(@PathVariable String userId) {
        log.info("Unban user: {}", userId);
        adminUserService.unbanUser(userId);
        return APIResponse.<String>builder()
                .message("Unban user successfully")
                .build();
    }

    @PutMapping("/{userId}/role")
    public APIResponse<String> updateRole(
            @PathVariable String userId,
            @RequestParam String roleName) {
        log.info("Update role: {}for user: {}", roleName, userId);
        adminUserService.updateUserRole(userId, roleName);
        return APIResponse.<String>builder()
                .message("Update role successfully")
                .build();
    }
    @GetMapping("/{userId}/details")
    public APIResponse<UserDetailResponse> getUserApplicationDetail(@PathVariable String userId) {
        log.info("Get user application detail: {}", userId);
        return APIResponse.<UserDetailResponse>builder()
                .message("Get user application detail successfully")
                .result(adminUserService.getUserApplicationDetail(userId))
                .build();
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "createdAT";
        String sortDir = sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
