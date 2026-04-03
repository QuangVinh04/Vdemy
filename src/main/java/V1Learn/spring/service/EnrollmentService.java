package V1Learn.spring.service;

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
import V1Learn.spring.repository.LessonProgressRepository;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentService {

        EnrollmentRepository enrollmentRepository;
        UserRepository userRepository;
        CourseRepository courseRepository;
        LessonProgressRepository lessonProgressRepository;
        EnrollmentMapper enrollmentMapper;
        CourseAccessService courseAccessService;

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
                                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));

                int totalLessons = (int) courseRepository.countLessonsByCourseId(courseId);

                return enrollmentMapper.from(enrollment, totalLessons);
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
