package V1Learn.spring.service;

import V1Learn.spring.dto.response.ProgressUpdateResponse;
import V1Learn.spring.entity.*;
import V1Learn.spring.enums.EnrollmentStatus;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.repository.*;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonProgressService {

    EnrollmentRepository enrollmentRepository;
    LessonProgressRepository lessonProgressRepository;
    LessonRepository lessonRepository;
    CourseRepository courseRepository;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ProgressUpdateResponse markLessonAsComplete(String enrollmentId, String lessonId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        // Kiểm tra ownership trước khi tạo progress
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Kiểm tra Lesson tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Kiểm tra Lesson có thuộc về Course của Enrollment này không
        if (!lesson.getChapter().getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new AppException(ErrorCode.LESSON_NOT_BELONG_TO_COURSE);
        }

        LessonProgress lp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .isCompleted(false)
                        .lastWatchedAt(0)
                        .build());

        lp.setIsCompleted(true);
        lp.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lp);

        Enrollment updatedEnrollment = updateOverallEnrollmentProgress(enrollmentId);

        // Tìm bài tiếp theo theo thứ tự chapter + lesson
        String nextLessonId = lessonRepository.findNextLessonId(
                lesson.getChapter().getCourse().getId(),
                lesson.getChapter().getOrderIndex(),
                lesson.getOrderIndex()).orElse(null);

        return ProgressUpdateResponse.builder()
                .enrollmentId(enrollmentId)
                .progressPercentage(updatedEnrollment.getProgressPercentage())
                .courseStatus(updatedEnrollment.getStatus())
                .isLessonCompleted(true)
                .nextLessonId(nextLessonId)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Enrollment updateOverallEnrollmentProgress(String enrollmentId) {

        Enrollment e = enrollmentRepository.findByIdWithCourse(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));

        // Lấy tổng số bài học của khóa học
        long totalLessons = courseRepository.countLessonsByCourseId(e.getCourse().getId());

        // Xử lý trường hợp course chưa có lesson nào
        if (totalLessons == 0) {
            e.setProgressPercentage(0.0);
            e.setLastAccessedAt(LocalDateTime.now());
            return enrollmentRepository.save(e);
        }

        // Đếm số bài học đã hoàn thành của user trong khóa này
        long completedCount = lessonProgressRepository.countByEnrollmentIdAndIsCompletedTrue(e.getId());

        // Tính toán phần trăm
        double percentage = ((double) completedCount / totalLessons) * 100;

        // Làm tròn 2 chữ số thập phân
        percentage = Math.round(percentage * 100.0) / 100.0;

        // Cập nhật vào bảng Enrollment
        e.setProgressPercentage(percentage);
        e.setLastAccessedAt(LocalDateTime.now());

        // Kiểm tra nếu đã xong 100% thì đổi Status
        if (percentage >= 100.0) {
            e.setStatus(EnrollmentStatus.COMPLETED);
            e.setCompletedAt(LocalDateTime.now());
        } else if (e.getStatus() == EnrollmentStatus.ENROLLED) {
            e.setStatus(EnrollmentStatus.IN_PROGRESS);
        }
        return enrollmentRepository.save(e);
    }

    private LessonProgress createNewProgress(String enrollmentId, String lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));

        // Giả sử bạn có LessonRepository để tìm bài học
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        return LessonProgress.builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .isCompleted(false)
                .lastWatchedAt(0)
                .build();
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateWatchProgress(String enrollmentId, String lessonId, Integer seconds) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        // Kiểm tra ownership trước
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        LessonProgress lp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> createNewProgress(enrollmentId, lessonId));

        lp.setLastWatchedAt(seconds);

        // Cập nhật lastAccessedAt và trạng thái (chỉ 1 lần)
        enrollment.setLastAccessedAt(LocalDateTime.now());
        if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        }
        enrollmentRepository.save(enrollment);
        lessonProgressRepository.save(lp);
    }

}
