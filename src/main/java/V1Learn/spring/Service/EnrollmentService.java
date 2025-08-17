//package V1Learn.spring.Service;
//
//
//import V1Learn.spring.DTO.Response.CheckEnrolledResponse;
//import V1Learn.spring.Entity.Course;
//import V1Learn.spring.Entity.Enrollment;
//import V1Learn.spring.Entity.User;
//import V1Learn.spring.Exception.AppException;
//import V1Learn.spring.Exception.ErrorCode;
//import V1Learn.spring.Repostiory.CourseRepository;
//import V1Learn.spring.Repostiory.EnrollmentRepository;
//import V1Learn.spring.Repostiory.UserRepository;
//import V1Learn.spring.utils.*;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class EnrollmentService {
//
//    EnrollmentRepository enrollmentRepository;
//    UserRepository userRepository;
//    private final CourseRepository courseRepository;
//
//    @PreAuthorize("isAuthenticated()")
//    public CheckEnrolledResponse checkEnrolled(String courseId) {
//        String userId = SecurityUtils.getCurrentUserId()
//                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
//        log.info("price:{}", course.getPrice());
//        if(course.getPrice().compareTo(BigDecimal.ZERO) == 0) {
//            return CheckEnrolledResponse.builder()
//                    .currentUserId(userId)
//                    .valid(true)
//                    .build();
//        }
//        List<Enrollment> enrollment = enrollmentRepository.findByUserAndCourseId(user, courseId);
//        if(!enrollment.isEmpty()) {
//            for(Enrollment e : enrollment) {
//                if (e.getStatus().equals(EnrollmentStatus.COMPLETED)) {
//                    return CheckEnrolledResponse.builder()
//                            .currentUserId(userId)
//                            .valid(true)
//                            .build();
//                }
//            }
//        }
//        return CheckEnrolledResponse.builder()
//                .currentUserId(null)
//                .valid(false)
//                .build();
//    }
//
//
//
//
//
//
//
//}
