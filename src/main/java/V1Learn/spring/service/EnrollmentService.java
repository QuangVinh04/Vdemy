package V1Learn.spring.service;

import V1Learn.spring.dto.response.CheckEnrolledResponse;
import V1Learn.spring.dto.response.EnrollmentResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.entity.Course;
import V1Learn.spring.entity.Enrollment;
import V1Learn.spring.entity.User;
import V1Learn.spring.enums.AccessType;
import V1Learn.spring.enums.EnrollmentStatus;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.EnrollmentMapper;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.EnrollmentRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.utils.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentService {

    EnrollmentRepository enrollmentRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    EnrollmentMapper enrollmentMapper;
    CourseAccessService courseAccessService;

    // @PreAuthorize("isAuthenticated()")
    // public CheckEnrolledResponse checkEnrolled(String courseId) {

    // String userId = SecurityUtils.getCurrentUserId()
    // .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

    // User user = userRepository.findById(userId)
    // .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    // Course course = courseRepository.findById(courseId)
    // .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

    // // Course miễn phí cho phép truy cập
    // if (course.getPrice().compareTo(BigDecimal.ZERO) == 0) {
    // return CheckEnrolledResponse.builder()
    // .currentUserId(userId)
    // .valid(true)
    // .build();
    // }
    // // Kiểm tra enrollment
    // List<Enrollment> enrollment =
    // enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
    // .orElse(new ArrayList<>());

    // if (!enrollment.isEmpty()) {
    // for (Enrollment e : enrollment) {
    // return CheckEnrolledResponse.builder()
    // .currentUserId(userId)
    // .valid()
    // .build();

    // }
    // }
    // return CheckEnrolledResponse.builder()
    // .currentUserId(null)
    // .valid(false)
    // .build();
    // }

    /**
     * Kiểm tra user đang học course
     */
    public boolean isEnrolled(String userId, String courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElse(new ArrayList<>());

        return enrollments.stream()
                .anyMatch(e -> e.getStatus() == EnrollmentStatus.ENROLLED ||
                        e.getStatus() == EnrollmentStatus.IN_PROGRESS ||
                        e.getStatus() == EnrollmentStatus.COMPLETED);
    }

    @Transactional
    public void ensureEnrollment(User user, Course course) {

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            log.info("User {} already enrolled in course {}", user.getId(), course.getId());
            return;
        }

        Enrollment e = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        enrollmentRepository.save(e);
        log.info("User {} enrolled in course {}", user.getId(), course.getId());

        if (course.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                courseAccessService.grantAccess(course.getId(), user.getId(), AccessType.FREE_TRIAL);
        }
    }

    // lấy tiến độ 1 khóa
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public EnrollmentResponse getProgressForCurrentUser(String courseId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST))
                .get(0); // nếu bạn để Optional<List>

        return enrollmentMapper.from(enrollment);
    }

    // danh sách enrollments
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public PageResponse<?> getMyEnrollments(Pageable pageable) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<Enrollment> enrollments = enrollmentRepository.findByUserId(userId, pageable);
        // map sang DTO
        List<EnrollmentResponse> items = enrollments.stream()
                .map(enrollmentMapper::summary)
                .toList();

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(enrollments.getTotalPages())
                .items(items)
                .build();
    }

}
