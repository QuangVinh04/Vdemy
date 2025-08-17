package V1Learn.spring.controller;

import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.CourseResponse;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.Service.CourseDraftService;
import V1Learn.spring.Service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@Slf4j
public class CourseController {
    CourseService courseService;
    CourseDraftService courseDraftService;

//    @PostMapping(value = "/create-course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public APIResponse<?> createCourse(
//            @RequestPart("courseRequest") CourseCreationRequest request,
//            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
//            @RequestPart(value = "video", required = false) MultipartFile introVideo) {
//        log.info("Request creating new course");
//        var result = courseService.createNewCourse(request, thumbnail, introVideo);
//        return APIResponse.builder()
//                .result(result)
//                .message("Create new course successfully")
//                .build();
//    }

    @PostMapping(value = "/create-course")
    public APIResponse<CourseResponse> createCourse(
            @RequestBody CourseCreationRequest request) {
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
            @RequestBody CourseUpdateRequest request) {
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

    @PostMapping("/course/create-preview")
    APIResponse<?> createPreview(@ModelAttribute CourseUpdateRequest request,
                                @RequestParam(required = false) String courseId) {
        log.info("Controller: create Preview");
        return APIResponse.builder()
                .result(courseDraftService.savePreview(courseId, request))
                .build();
    }

    @GetMapping("/course/get-preview")
    APIResponse<?> getPreview(String courseId) {
        log.info("Controller: get preview Courses");
        return APIResponse.builder()
                .result(courseDraftService.getPreview(courseId))
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


    @GetMapping(path = "/courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getAllCourses(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request all courses");
        return APIResponse.builder()
                .result(courseService.getAllCourses(pageable))
                .message("Get all course successful")
                .build();
    }

    @GetMapping(path = "/teacher/courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getCoursesByTeacher(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request courses by logged-in teacher");
        return APIResponse.builder()
                .result(courseService.getCourseByTeacher(pageable))
                .message("Get courses by teacher successful")
                .build();
    }

    @GetMapping(path = "/user/me/courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getCoursesByUser(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request courses by logged-in user");
        return APIResponse.builder()
                .result(courseService.getCourseByUser(pageable))
                .message("Get courses by user successful")
                .build();
    }



    @GetMapping("/course/{courseId}")
    public APIResponse<?> getCourseById(@PathVariable String courseId) {
        log.info("Request course by ID: {}", courseId);
        return APIResponse.builder().result(courseService.getCourseById(courseId)).build();
    }

    @GetMapping("/course-detail/{courseId}")
    public APIResponse<?> getCourseDetail(@PathVariable String courseId) {
        log.info("Request course detail by ID: {}", courseId);
        return APIResponse.builder()
                .result(courseService.getBasicCourseInfo(courseId))
                .message("Get course detail successful")
                .build();
    }



    @GetMapping(path = "/search-course-by-specification")
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
}



