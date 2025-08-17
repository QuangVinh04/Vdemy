package V1Learn.spring.controller.admin;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.PageResponse;


import V1Learn.spring.DTO.Response.admin.AdminCourseResponse;
import V1Learn.spring.Service.admin.AdminCourseService;
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
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCourseController {

    AdminCourseService adminCourseService;

    @GetMapping
    public APIResponse<PageResponse<List<AdminCourseResponse>>> getAllCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt, asc") String[] sort) {

        log.info("Get all courses by admin");
        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        return APIResponse.<PageResponse<List<AdminCourseResponse>>>builder()
                .message("Get all courses successfully")
                .result(adminCourseService.getAllCourses(pageable))
                .build();
    }



    @PostMapping("/{courseId}/ban")
    public APIResponse<String> banCourse(@PathVariable String courseId) {
        log.info("Ban course by admin");
        adminCourseService.banCourse(courseId);
        return APIResponse.<String>builder()
                .message("Course banned successfully")
                .build();
    }

    @PostMapping("/{courseId}/unban")
    public APIResponse<String> unbanCourse(@PathVariable String courseId) {
        log.info("Unban course by admin");
        adminCourseService.unbanCourse(courseId);
        return APIResponse.<String>builder()
                .message("Course unbanned successfully")
                .build();
    }

    @GetMapping("/banned")
    public APIResponse<PageResponse<List<AdminCourseResponse>>> getAllCoursesBanned(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt, asc") String[] sort) {
        log.info("Get all courses banned by admin");
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));

        return APIResponse.<PageResponse<List<AdminCourseResponse>>>builder()
                .message("Get all courses banned successfully")
                .result(adminCourseService.getBannedCourses(pageable))
                .build();
    }

    @GetMapping("/active")
    public APIResponse<PageResponse<List<AdminCourseResponse>>> getAllCourseActive(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt, asc") String[] sort) {
        log.info("Get all courses active by admin");
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));

        return APIResponse.<PageResponse<List<AdminCourseResponse>>>builder()
                .message("Get all courses active successfully")
                .result(adminCourseService.getActiveCourses(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public APIResponse<AdminCourseResponse> getCourseDetails(@PathVariable String id) {
        log.info("Get course details by admin");
        return APIResponse.<AdminCourseResponse>builder()
                .message("Get course details successfully")
                .result(adminCourseService.getCourseDetails(id))
                .build();
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "title";
        String sortDir = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}