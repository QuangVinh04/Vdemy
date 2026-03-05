package V1Learn.spring.dto.response;


import V1Learn.spring.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {

    // Thông tin Enrollment
    String enrollmentId;
    EnrollmentStatus status;
    Double progressPercentage;
    LocalDateTime enrolledAt; // createdAt từ AbstractEntity
    LocalDateTime completedAt;
    LocalDateTime lastAccessedAt;

    // Thông tin Course cơ bản
    String courseId;
    String courseTitle;
    String courseThumbnailUrl;
    String courseDescription;
    String instructorName;
    String instructorAvatar;
    String instructorId;

    // Thống kê học tập
    Integer totalLessons;
    Integer completedLessons;
    Long totalDuration; // tổng thời lượng course (seconds)
    Long watchedDuration; // tổng thời gian đã xem (seconds)




}
