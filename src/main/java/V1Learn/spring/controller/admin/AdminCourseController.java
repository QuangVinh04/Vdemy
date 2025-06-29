//package V1Learn.spring.controller.admin;
//
//import V1Learn.spring.DTO.Response.PageResponse;
//
//
//import V1Learn.spring.Service.admin.AdminCourseService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/admin/courses")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class AdminCourseController {
//
//    AdminCourseService courseService;
//
//    @GetMapping
//    public ResponseEntity<PageResponse> getAllCouses(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {
//
//        // Điều chỉnh page để Spring bắt đầu từ 0 (page - 1)
//        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
//        return ResponseEntity.ok(courseService.getAllCourses(pageable));
//    }
//
//
//
//    @PostMapping("/{courseId}/ban")
//    public ResponseEntity<String> banCourse(@PathVariable String courseId) {
//        courseService.banCourse(courseId);
//        return ResponseEntity.ok("Course banned successfully.");
//    }
//
//    @PostMapping("/{courseId}/unban")
//    public ResponseEntity<String> unbanCourse(@PathVariable String courseId) {
//        courseService.unbanCourse(courseId);
//        return ResponseEntity.ok("Course unbanned successfully.");
//    }
//
//    @GetMapping("/banned")
//    public ResponseEntity<PageResponse> getAllCousesBanned(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {
//
//        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
//
//        return ResponseEntity.ok(courseService.getBannedCourses(pageable));
//    }
//
//    @GetMapping("/active")
//    public ResponseEntity<PageResponse> getAllCousesActive(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAT, asc") String[] sort) {
//
//        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
//
//        return ResponseEntity.ok(courseService.getActiveCourses(pageable));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Admin_CourseResponse> getCourseDetails(@PathVariable String id) {
//        Admin_CourseResponse response = courseService.getCourseDetails(id);
//        return ResponseEntity.ok(response);
//    }
//
//    private Sort getSortOrder(String[] sort) {
//        String sortBy = sort.length > 0 ? sort[0] : "title";
//        String sortDir = sort.length > 1 ? sort[1] : "asc";
//        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//        return Sort.by(direction, sortBy);
//    }
//}