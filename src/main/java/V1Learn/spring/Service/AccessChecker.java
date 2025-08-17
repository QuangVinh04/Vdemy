//package V1Learn.spring.Service;
//
//import V1Learn.spring.Entity.*;
//import V1Learn.spring.Exception.AppException;
//import V1Learn.spring.Exception.ErrorCode;
//import V1Learn.spring.Repostiory.ChapterRepository;
//import V1Learn.spring.Repostiory.EnrollmentRepository;
//import V1Learn.spring.utils.EnrollmentStatus;
//import V1Learn.spring.utils.PaymentStatus;
//import V1Learn.spring.utils.SecurityUtils;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
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
//public class AccessChecker {
//
//    ChapterRepository chapterRepository;
//    EnrollmentRepository enrollmentRepository;
//
//
//    public boolean userHasLessonAccess(String userId, String chapterId) {
//        if (userId == null || chapterId == null) return false;
//
//        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
//        if (chapter == null) return false;
//
//        String courseId = chapter.getCourse().getId();
//        Course course = chapter.getCourse();
//
//        Payment payment = null;
//
//        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
//        if(!enrollments.isEmpty()){
//            for(Enrollment e : enrollments){
//                if(e.getStatus().equals(EnrollmentStatus.COMPLETED)){
//                    payment = e.getPayment();
//                    break;
//                }
//            }
//        }
//
//        return SecurityUtils.hasRole("ADMIN")
//                || (course.getInstructor().getId().equals(userId))
//                || (payment != null && payment.getStatus().equals(PaymentStatus.COMPLETED))
//                || (course.getPrice().equals(BigDecimal.ZERO));
//    }
//}
