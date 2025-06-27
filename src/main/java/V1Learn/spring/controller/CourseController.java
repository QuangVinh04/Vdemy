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

    @PostMapping(value = "/create-course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> createCourse(
            @RequestPart("courseRequest") CourseCreationRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "video", required = false) MultipartFile introVideo,
            @RequestPart(value = "lessonVideos", required = false) Map<String, MultipartFile> lessonVideos) {
        log.info("Creating new course");
        var result = courseService.createCourse(request, thumbnail, introVideo, lessonVideos);
        return APIResponse.builder().result(result).build();
    }

    @PutMapping(value = "/update-course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<CourseResponse> updateCourseById(
            @PathVariable String courseId,
            @RequestPart("courseRequest") CourseUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "video", required = false) MultipartFile introVideo,
            @RequestPart(value = "lessonVideos", required = false) Map<String, MultipartFile> lessonVideos) {
        log.info("Updating course with ID: {}", courseId);
        return APIResponse.<CourseResponse>builder()
                .result(courseService.updateCourse(courseId, request, thumbnail, introVideo, lessonVideos))
                .build();
    }

//    @PostMapping("/course/create-preview")
//    APIResponse<?> createPreview(@ModelAttribute CourseUpdateRequest request,
//                                @RequestParam(required = false) String courseId) {
//        log.info("Controller: create Preview");
//        return APIResponse.builder()
//                .result(courseDraftService.savePreview(courseId, request))
//                .build();
//    }

//    @GetMapping("/course/get-preview")
//    APIResponse<?> getPreview(String courseId) {
//        log.info("Controller: get preview Courses");
//        return APIResponse.builder()
//                .result(courseDraftService.getPreview(courseId))
//                .build();
//    }

    @DeleteMapping("/delete-course/{courseId}")
    public APIResponse<String> deleteCourseById(@PathVariable String courseId) {
        log.info("Deleting course with ID: {}", courseId);
        courseService.deleteCourse(courseId);
        return APIResponse.<String>builder().result("Course has been deleted").build();
    }


    @GetMapping(path = "/courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> getAllCourses(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Fetching all courses");
        return APIResponse.builder().result(courseService.getAllCourses(pageable)).build();
    }

    @GetMapping("/teacher/courses")
    public APIResponse<?> getCoursesByTeacher(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Fetching courses by logged-in teacher");
        return APIResponse.builder().result(courseService.getCourseByTeacher(pageable)).build();
    }

    @GetMapping("/user/me/courses")
    public APIResponse<?> getCoursesByUser(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Fetching courses by logged-in user");
        return APIResponse.builder().result(courseService.getCourseByUser(pageable)).build();
    }



    @GetMapping("/course/{courseId}")
    public APIResponse<?> getCourseById(@PathVariable String courseId) {
        log.info("Fetching course by ID: {}", courseId);
        return APIResponse.builder().result(courseService.getCourseById(courseId)).build();
    }

    @GetMapping("/course-detail/{courseId}")
    public APIResponse<?> getCourseDetail(@PathVariable String courseId) {
        log.info("Fetching course detail by ID: {}", courseId);
        return APIResponse.builder().result(courseService.getCourseDetail(courseId)).build();
    }



    @GetMapping(path = "/search-course-by-specification")
    public APIResponse<PageResponse> searchCourses(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String[] course) {
        log.info("Searching courses with filters");
        return APIResponse.<PageResponse>builder()
                .result(courseService.advanceSearchWithSpecifications(pageable, course))
                .build();
    }
}



