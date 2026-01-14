package V1Learn.spring.controller.admin;


import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.dto.response.admin.AdminTeacherResponse;
import V1Learn.spring.service.admin.AdminTeacherService;
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
@RequestMapping("/api/v1/admin/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTeacherController {

    AdminTeacherService teacherService;

    @GetMapping
    public APIResponse<PageResponse<List<AdminTeacherResponse>>> getAllTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {

        log.info("Get all teachers by admin");
        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        return APIResponse.<PageResponse<List<AdminTeacherResponse>>>builder()
                .message("Get all teachers successfully")
                .result(teacherService.getAllTeachers(pageable))
                .build();
    }


    @PutMapping("/{userId}/remove-role")
    public APIResponse<String> removeTeacherRole(@PathVariable String userId) {
        log.info("Remove teacher role by admin");
        teacherService.removeTeacherRole(userId);
        return APIResponse.<String>builder()
                .message("User role updated to USER and registration status set to null successfully")
                .build();
    }


    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "name";
        String sortDir = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
