//package V1Learn.spring.controller;
//
//import V1Learn.spring.DTO.Response.APIResponse;
//import V1Learn.spring.DTO.Response.CheckEnrolledResponse;
//import V1Learn.spring.Service.EnrollmentService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequestMapping("/api/v1")
//@Slf4j
//public class EnrollmentController {
//    EnrollmentService enrollmentService;
//
//
//
//    @GetMapping("/check-enrolled/{courseId}")
//    APIResponse<CheckEnrolledResponse> checkEnrolled(@PathVariable String courseId) {
//        log.info("Controller: check enrolled");
//        return APIResponse.<CheckEnrolledResponse>builder()
//                .result(enrollmentService.checkEnrolled(courseId))
//                .build();
//    }
//
//
//}
