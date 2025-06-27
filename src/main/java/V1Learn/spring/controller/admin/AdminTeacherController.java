package V1Learn.spring.controller.admin;


import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.Service.admin.AdminTeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTeacherController {

    AdminTeacherService teacherService;

    @GetMapping
    public ResponseEntity<PageResponse> getAllTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {

        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        return ResponseEntity.ok(teacherService.getAllTeachers(pageable));
    }




    @PutMapping("/{userId}/remove-role")
    public ResponseEntity<String> removeTeacherRole(@PathVariable String userId) {
        teacherService.removeTeacherRole(userId);
        return ResponseEntity.ok("User role updated to USER and registration status set to null successfully");
    }


    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "name";
        String sortDir = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
