package V1Learn.spring.controller;

import V1Learn.spring.service.EnrollmentService;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.CheckEnrolledResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/enrollments")
@Slf4j
public class EnrollmentController {
    EnrollmentService enrollmentService;

    // @GetMapping("/check-enrolled/{courseId}")
    // APIResponse<CheckEnrolledResponse> checkEnrolled(@PathVariable String courseId) {
    //     log.info("Controller: check enrolled");
    //     return APIResponse.<CheckEnrolledResponse>builder()
    //             .result(enrollmentService.checkEnrolled(courseId))
    //             .build();
    // }

    // Lấy tiến độ học 1 khóa
    @GetMapping("/{courseId}/progress")
    public APIResponse<?> getEnrollmentProgress(@PathVariable String courseId) {
        log.info("Getting enrollment progress for course: {}", courseId);
        return APIResponse.builder()
                .result(enrollmentService.getProgressForCurrentUser(courseId))
                .message("Get enrollment progress successfully")
                .build();
    }

    // Lấy danh sách khóa học đã ghi danh của user hiện tại
    @GetMapping("/my-enrollments")
    public APIResponse<?> getMyEnrollments(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting enrollments for current user");
        return APIResponse.builder()
                .result(enrollmentService.getMyEnrollments(pageable))
                .message("Get enrollments successfully")
                .build();
    }

}
