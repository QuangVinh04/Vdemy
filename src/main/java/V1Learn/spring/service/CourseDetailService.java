package V1Learn.spring.service;


import V1Learn.spring.dto.response.*;
import V1Learn.spring.entity.*;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.repository.*;
import V1Learn.spring.projection.ChapterSummaryProjection;
import V1Learn.spring.projection.CourseStatsProjection;
import V1Learn.spring.projection.LessonSummaryProjection;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseDetailService {

    CourseRepository courseRepository;
    ChapterRepository chapterRepository;
    LessonRepository lessonRepository;
    EnrollmentRepository enrollmentRepository;
    LessonProgressRepository lessonProgressRepository;
    LessonService lessonService;
    CourseAccessService courseAccessService;

    CourseCacheService courseCacheService;

    @Qualifier("taskExecutor")
    Executor taskExecutor;

    private static final String CHAPTERS = "chapters";
    private static final String LESSONS = "chapters.lessons";
    private static final String STATS = "stats";
    private static final String INSTRUCTOR = "instructor";
    private static final String PROGRESS = "progress";

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(String courseId, Set<String> fields, boolean previewMode) {

        Course course = courseRepository.findBasicInfoById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        String userId = SecurityUtils.getCurrentUserId().orElse(null);
        AccessDecision accessResult = courseAccessService.checkUserAccess(courseId, userId);

        String instructorId = course.getInstructor() != null ? course.getInstructor().getId() : null;
        if (previewMode){
            if(!Objects.equals(instructorId, userId)){
                throw new AppException(ErrorCode.AUTH_FORBIDDEN);
            }
            accessResult = AccessDecision.builder()
                    .hasFullAccess(true)
                    .isInstructor(true)
                    .accessType("INSTRUCTOR_PREVIEW")
                    .build();
        }

        // 1. Khởi tạo các biến Future với giá trị mặc định
        CompletableFuture<List<ChapterSummaryProjection>> chaptersFuture = CompletableFuture.completedFuture(Collections.emptyList());
        CompletableFuture<Map<String, List<LessonSummaryProjection>>> lessonsFuture = CompletableFuture.completedFuture(Collections.emptyMap());
        CompletableFuture<CourseStatsProjection> statsFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<InstructorInfo> instructorFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<Set<String>> progressFuture = CompletableFuture.completedFuture(Collections.emptySet());

        // 2. Kích hoạt xử lý đa luồng (kết hợp lấy từ Cache)
        if (fields.contains(CHAPTERS) || fields.contains(LESSONS) || fields.contains(STATS)) {
            chaptersFuture = CompletableFuture.supplyAsync(() ->
                    courseCacheService.loadChapterSummaries(courseId), taskExecutor);
        }

        if (fields.contains(LESSONS)) {
            // Luồng lấy bài học sẽ chờ luồng lấy chương hoàn thành (Chaining)
            lessonsFuture = chaptersFuture.thenApplyAsync(chapters ->
                    courseCacheService.loadLessons(courseId, chapters), taskExecutor);
        }

        if (fields.contains(STATS)) {
            statsFuture = CompletableFuture.supplyAsync(() ->
                    courseCacheService.loadCourseStats(courseId), taskExecutor);
        }

        if (fields.contains(INSTRUCTOR) && course.getInstructor() != null) {
            String insId = course.getInstructor().getId();
            String insName = course.getInstructor().getFullName();
            String insAvatar = course.getInstructor().getAvatarUrl();
            instructorFuture = CompletableFuture.supplyAsync(() ->
                    courseCacheService.loadInstructorInfo(insId, insName, insAvatar), taskExecutor);
        }

        if (fields.contains(PROGRESS)) {
            // Dữ liệu người dùng (Động) -> Vẫn truy vấn DB bình thường, không Cache
            final AccessDecision finalAccess = accessResult;
            progressFuture = CompletableFuture.supplyAsync(() ->
                    loadUserProgress(finalAccess, courseId, userId), taskExecutor);
        }

        // 3. Đợi tất cả các luồng hoàn tất
        CompletableFuture.allOf(chaptersFuture, lessonsFuture, statsFuture, instructorFuture, progressFuture).join();

        // 4. Build Response
        return buildCourseDetailResponse(
                course, accessResult, fields,
                chaptersFuture.join(),
                lessonsFuture.join(),
                statsFuture.join(),
                instructorFuture.join(),
                progressFuture.join()
        );
    }

    private Set<String> loadUserProgress(AccessDecision accessResult, String courseId, String userId) {
        if (!accessResult.isHasFullAccess() || userId == null || userId.isEmpty()) {
            return Collections.emptySet();
        }
        return lessonProgressRepository.findCompletedLessonIdsByUserAndCourse(userId, courseId);
    }

    /**
     * Build response đã bỏ DataContainer, truyền tham số trực tiếp
     */
    private CourseDetailResponse buildCourseDetailResponse(
            Course course, AccessDecision accessResult, Set<String> fields,
            List<ChapterSummaryProjection> chapterSummaries,
            Map<String, List<LessonSummaryProjection>> lessonsByChapter,
            CourseStatsProjection courseStats,
            InstructorInfo instructorInfo,
            Set<String> completedLessons) {

        CourseDetailResponse courseDetailResponse = CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .language(course.getLanguage())
                .level(course.getLevel())
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .price(course.getPrice() != null ? course.getPrice().longValue() : 0L)
                .duration(course.getTotalDuration() != null ? course.getTotalDuration().toString() : "0")
                .thumbnailUrl(course.getThumbnailUrl())
                .videoUrl(course.getVideoUrl())
                .status(course.getStatus())
                .accessInfo(courseAccessService.buildAccessInfo(accessResult, course))
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();

        // Build chapters
        if(fields.contains(CHAPTERS)) {
            List<ChapterResponse> chapters = chapterSummaries.stream().map(ch -> {
                ChapterResponse response = ChapterResponse.builder()
                        .id(ch.id())
                        .title(ch.title())
                        .description(ch.description())
                        .orderIndex(ch.orderIndex())
                        .totalLessons(ch.totalLessons())
                        .totalDuration(ch.totalDurationSeconds())
                        .build();

                if(fields.contains(LESSONS) && lessonsByChapter != null) {
                    List<LessonSummaryProjection> lessonSummaries = lessonsByChapter.getOrDefault(ch.id(), Collections.emptyList());
                    Set<LessonResponse> lessons = lessonSummaries.stream()
                            .map(ls -> lessonService.buildLesson(ls, accessResult, completedLessons != null ? completedLessons : Collections.emptySet()))
                            .collect(Collectors.toSet());
                    response.setLessons(lessons);
                }
                return response;
            }).toList();

            courseDetailResponse.setChapters(chapters);
        }

        // Build Stats
        if (fields.contains(STATS)) {
            long totalChapters = chapterSummaries != null ? chapterSummaries.size() : 0L;
            long totalLessons = chapterSummaries != null
                    ? chapterSummaries.stream().mapToLong(ChapterSummaryProjection::totalLessons).sum() : 0L;

            CourseStats stats = CourseStats.builder()
                    .totalStudents(courseStats != null ? courseStats.totalStudents() : 0)
                    .rating(courseStats != null ? courseStats.rating() : 0.0)
                    .totalReviews(courseStats != null ? courseStats.totalReviews() : 0)
                    .totalChapters(totalChapters)
                    .totalLessons(totalLessons)
                    .totalDuration(course.getTotalDuration())
                    .build();
            courseDetailResponse.setStats(stats);
        }

        // Build Instructor
        if(fields.contains(INSTRUCTOR)) {
            courseDetailResponse.setInstructor(instructorInfo);
        }

        return courseDetailResponse;
    }
}

