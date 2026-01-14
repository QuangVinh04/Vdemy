package V1Learn.spring.controller;

import V1Learn.spring.dto.request.*;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.CourseResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.service.CourseDetailService;
import V1Learn.spring.service.CourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
@Slf4j
public class CourseController {
    CourseService courseService;
    CourseDetailService courseDetailService;



    @PostMapping(value = "/create-course")
    public APIResponse<CourseResponse> createCourse(
            @RequestBody @Valid CourseCreationRequest request) {
        log.info("Request creating new course");
        var result = courseService.createNewCourse(request);
        return APIResponse.<CourseResponse>builder()
                .result(result)
                .message("Create new course successfully")
                .build();
    }



    @PutMapping(value = "/update-course/{courseId}")
    public APIResponse<CourseResponse> updateCourse(
            @PathVariable String courseId,
            @RequestBody @Valid CourseUpdateRequest request) {
        log.info("Request updating course with ID: {}", courseId);
        return APIResponse.<CourseResponse>builder()
                .result(courseService.updateCourse(courseId, request))
                .build();
    }

    @PutMapping(value = "/publish-course/{courseId}")
    public APIResponse<CourseResponse> publish(@PathVariable String courseId) {
        log.info("Request publish course with ID: {}", courseId);
        courseService.publish(courseId);
        return APIResponse.<CourseResponse>builder()
                .message("Create new course successfully")
                .build();
    }



    @GetMapping("/{courseId}/preview")
    APIResponse<?> getPreview(@PathVariable String courseId) {
        log.info("Controller: get preview Courses");
        Set<String> fields = Set.of("basic", "instructor",
                "stats",
                "chapters",
                "chapters.lessons");
        return APIResponse.builder()
                .result(courseDetailService.getCourseDetail(courseId, fields, true))
                .build();
    }

    @DeleteMapping("/delete-course/{courseId}")
    public APIResponse<String> deleteCourseById(@PathVariable String courseId) {
        log.info("Request deleting course with ID: {}", courseId);
        courseService.deleteCourse(courseId);
        return APIResponse.<String>builder()
                .result("Course has been deleted")
                .build();
    }


    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getAllCourses(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request all courses");
        return APIResponse.builder()
                .result(courseService.getAllCourses(pageable))
                .message("Get all course successful")
                .build();
    }

    @GetMapping(path = "/teacher", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getCoursesByTeacher(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request courses by logged-in teacher");
        return APIResponse.builder()
                .result(courseService.getCourseByTeacher(pageable))
                .message("Get courses by teacher successful")
                .build();
    }

    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getCoursesByUser(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request courses by logged-in user");
        return APIResponse.builder()
                .result(courseService.getCourseByUser(pageable))
                .message("Get courses by user successful")
                .build();
    }



    @GetMapping("/{courseId}")
    public APIResponse<?> getCourseById(@PathVariable String courseId) {
        log.info("Request course by ID: {}", courseId);
        return APIResponse.builder().result(courseService.getCourseById(courseId)).build();
    }

    @GetMapping("/{courseId}/detail")
    public APIResponse<?> getCourseDetail(@PathVariable String courseId,
                                          @RequestParam(name = "fields", required = false) String fieldsParam){

        Set<String> fields = parseFields(fieldsParam);
        log.info("Request course detail by ID: {}", courseId);
        return APIResponse.builder()
                .result(courseDetailService.getCourseDetail(courseId, fields, false))
                .message("Get course detail successful")
                .build();
    }



    @GetMapping(path = "/search")
    public APIResponse<PageResponse> searchCourses(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String[] course) {
        log.info("Request searching courses with filters");
        return APIResponse.<PageResponse>builder()
                .result(courseService.advanceSearchWithSpecifications(pageable, course))
                .message("Search courses successful")
                .build();
    }


    private Set<String> parseFields(String fieldsParam) {
        if (fieldsParam == null || fieldsParam.trim().isEmpty()) {
            // Return default fields when no fields specified
            return getDefaultFields();
        }

        return Arrays.stream(fieldsParam.split(","))
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(Collectors.toSet());
    }

    private Set<String> getDefaultFields() {
        return Set.of(
                "basic",           // Course basic info
                "instructor",      // Basic instructor info
                "stats",          // Course statistics
                "chapters",       // Chapter structure
                "chapters.lessons" // Lessons within chapters
        );
    }
}



