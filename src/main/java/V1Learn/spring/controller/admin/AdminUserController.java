package V1Learn.spring.controller.admin;


import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.UserDetailResponse;
import V1Learn.spring.Service.admin.AdminUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    AdminUserService userService;

    @GetMapping
    public ResponseEntity<PageResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {

        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }


    @PostMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable String userId) {
        userService.banUser(userId);
        return ResponseEntity.ok("User banned successfully");
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable String userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok("User unbanned successfully");
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable String userId,
            @RequestParam String roleName) {
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated successfully");
    }
    @GetMapping("/{userId}/details")
    public ResponseEntity<UserDetailResponse> getUserApplicationDetail(@PathVariable String userId) {
        UserDetailResponse details = userService.getUserApplicationDetail(userId);
        return ResponseEntity.ok(details);
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "createdAT";
        String sortDir = sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
