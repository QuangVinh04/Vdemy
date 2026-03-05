package V1Learn.spring.service;

import V1Learn.spring.dto.response.InstructorInfo;
import V1Learn.spring.projection.ChapterSummaryProjection;
import V1Learn.spring.projection.CourseStatsProjection;
import V1Learn.spring.projection.LessonSummaryProjection;
import V1Learn.spring.repository.ChapterRepository;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.EnrollmentRepository;
import V1Learn.spring.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseCacheService {
    ChapterRepository chapterRepository;
    LessonRepository lessonRepository;
    CourseRepository courseRepository;
    EnrollmentRepository enrollmentRepository;

    @Cacheable(value = "course_chapter", key = "#courseId")
    public List<ChapterSummaryProjection> loadChapterSummaries(String courseId) {
        return chapterRepository.findChapterSummariesByCourseId(courseId);
    }

    @Cacheable(value = "course_lesson", key = "#courseId")
    public Map<String, List<LessonSummaryProjection>> loadLessons(String courseId) {
        List<LessonSummaryProjection> lessons = lessonRepository.findLessonsByCourseId(courseId);
        if (lessons.isEmpty())
            return Collections.emptyMap();
        return lessons.stream()
                .collect(Collectors.groupingBy(
                        LessonSummaryProjection::chapterId,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    @Cacheable(value = "course_stats", key = "#courseId")
    public CourseStatsProjection loadCourseStats(String courseId) {
        return courseRepository.getCourseStats(courseId)
                .orElse(new CourseStatsProjection(0L, 0.0, 0L));
    }

    @Cacheable(value = "course_instructor", key = "#instructorId")
    public InstructorInfo loadInstructorInfo(String instructorId, String fullName, String avatar) {
        return InstructorInfo.builder()
                .id(instructorId)
                .fullName(fullName)
                .avatar(avatar)
                .bio(null) // Lấy từ DB nếu cần
                .totalCourses(courseRepository.countByInstructorId(instructorId))
                .totalStudents(enrollmentRepository.countStudentsByInstructorId(instructorId))
                .build();
    }

    /**
     * Xóa cache chapter + lesson khi có thay đổi nội dung khóa học
     */
    @Caching(evict = {
            @CacheEvict(value = "course_chapter", key = "#courseId"),
            @CacheEvict(value = "course_lesson", key = "#courseId")
    })
    public void evictCourseContentCache(String courseId) {
        // Cache eviction handled by annotations
    }
}
