package V1Learn.spring.mapper;





import V1Learn.spring.dto.response.EnrollmentResponse;
import V1Learn.spring.entity.Enrollment;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    default EnrollmentResponse from(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        // Tính toán số lesson đã hoàn thành
        int completedLessons = 0;
        int totalLessons = 0;
        long watchedDuration = 0;

        if (enrollment.getLessonProgresses() != null) {
            totalLessons = enrollment.getLessonProgresses().size();
            completedLessons = (int) enrollment.getLessonProgresses().stream()
                    .filter(lp -> Boolean.TRUE.equals(lp.getIsCompleted()))
                    .count();
            watchedDuration = enrollment.getLessonProgresses().stream()
                    .mapToLong(lp -> lp.getWatchTime() != null ? lp.getWatchTime().longValue() : 0L)
                    .sum();
        }

        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage() != null
                        ? enrollment.getProgressPercentage()
                        : 0.0)
                .enrolledAt(enrollment.getCreatedAt()) // từ AbstractEntity
                .completedAt(enrollment.getCompletedAt())
                .lastAccessedAt(enrollment.getLastAccessedAt())

                // Course info
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .courseDescription(enrollment.getCourse().getDescription())
                .instructorName(enrollment.getCourse().getInstructor().getFullName())
                .instructorAvatar(enrollment.getCourse().getInstructor().getAvatar())
                .instructorId(enrollment.getCourse().getInstructor().getId())

                // Stats
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .totalDuration(enrollment.getCourse().getTotalDuration())
                .watchedDuration(watchedDuration)
                .build();
    }

    /**
     * lấy thông tin cơ bản
     */
    default EnrollmentResponse summary(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage() != null
                        ? enrollment.getProgressPercentage()
                        : 0.0)
                .enrolledAt(enrollment.getCreatedAt())
                .lastAccessedAt(enrollment.getLastAccessedAt())

                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .instructorName(enrollment.getCourse().getInstructor().getFullName())
                .instructorAvatar(enrollment.getCourse().getInstructor().getAvatar())
                .build();
    }


}
