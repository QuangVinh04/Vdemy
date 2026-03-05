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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.CompletableFuture;
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


    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(String courseId, Set<String> fields, boolean previewMode) {

        Course course = courseRepository.findCourseBasicInfo(courseId)
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


        DataContainer dataContainer = new DataContainer();

        loadRequestedData(course, fields, accessResult, userId, dataContainer);

        return buildCourseDetailResponse(course, accessResult, dataContainer, fields);
    }

    private void loadRequestedData(Course course, Set<String> fields,
                                   AccessDecision accessResult, String userId,
                                   DataContainer container) {

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        if(fields.contains("chapters")){
            futures.add(CompletableFuture.runAsync(() ->
                    container.chapterSummaries = loadChapterSummaries(course.getId())));
        }

        if(fields.contains("chapters.lessons")){
            futures.add(CompletableFuture.runAsync(() -> {
                if (container.chapterSummaries == null) {
                    container.chapterSummaries = loadChapterSummaries(course.getId());
                }
                container.lessonsByChapter = loadLessons(container.chapterSummaries);
            }));
        }

        if(fields.contains("stats")){
            futures.add(CompletableFuture.runAsync(() ->
                    container.courseStats = courseRepository.getCourseStats(course.getId()).orElse(null)));
        }

        if (fields.contains("instructor")) {
            futures.add(CompletableFuture.runAsync(() ->
                    container.instructorInfo = loadInstructorInfo(course)));
        }

        if (fields.contains("progress")) {
            futures.add(CompletableFuture.runAsync(() ->
                    container.completedLessons = loadUserProgress(accessResult, course.getId(), userId)));
        }


        // Wait for all async operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }

    private InstructorInfo loadInstructorInfo(Course course) {

        return InstructorInfo.builder()
                .id(course.getInstructor().getId())
                .fullName(course.getInstructor().getFullName())
                .avatar(course.getInstructor().getAvatar())
                .bio(null)
                .totalCourses(courseRepository.countByInstructorId(course.getInstructor().getId()))
                .totalStudents(enrollmentRepository.countStudentsByInstructorId(course.getInstructor().getId()))
                .build();
    }




    private List<ChapterSummaryProjection> loadChapterSummaries(String courseId) {
        return chapterRepository.findChapterSummariesByCourseId(courseId);
    }

    private Map<String, List<LessonSummaryProjection>> loadLessons(List<ChapterSummaryProjection> summaries) {
        if (summaries.isEmpty()) return Collections.emptyMap();
        List<String> chapterIds = summaries.stream().map(ChapterSummaryProjection::getId).toList();
        return lessonRepository.findLessonsByChapterIds(chapterIds)
                .stream()
                .collect(Collectors.groupingBy(LessonSummaryProjection::getChapterId));
    }

    private Set<String> loadUserProgress(AccessDecision accessResult, String courseId, String userId) {


        if (!accessResult.isHasFullAccess()) {
            return Collections.emptySet();
        }


        if (userId == null || userId.isEmpty()) {
            return Collections.emptySet();
        }

        return lessonProgressRepository
                .findCompletedLessonIdsByUserAndCourse(userId, courseId);
    }


    /**
     * Build response với thông tin đơn giản
     */
    private CourseDetailResponse buildCourseDetailResponse(
            Course course,
            AccessDecision accessResult,
            DataContainer container,
            Set<String> fields) {


        CourseDetailResponse courseDetailResponse = CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice().longValue())
                .thumbnailUrl(course.getThumbnailUrl())
                .accessInfo(courseAccessService.buildAccessInfo(accessResult, course))
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();


        // Build chapters
        if(fields.contains("chapters")){
        List<ChapterResponse> chapters = container.chapterSummaries.stream().map(ch -> {
            ChapterResponse response = ChapterResponse.builder()
                    .id(ch.getId())
                    .title(ch.getTitle())
                    .description(ch.getDescription())
                    .orderIndex(ch.getOrderIndex())
                    .totalLessons(ch.getTotalLessons())
                    .totalDuration(ch.getTotalDurationSeconds())
                    .build();

            if(fields.contains("chapters.lessons") && container.lessonsByChapter != null) {

                List<LessonSummaryProjection> lessonSummaries =
                        container.lessonsByChapter.getOrDefault(ch.getId(), Collections.emptyList());
                // Query đã ORDER BY rồi, không cần sort lại.

                List<LessonResponse> lessons = lessonSummaries.stream().map(ls -> lessonService.buildLesson(ls, accessResult,
                        container.completedLessons != null ? container.completedLessons : Collections.emptySet()))
                        .toList();
                response.setLessons(lessons);
            }
            return response;
            }).toList();

            courseDetailResponse.setChapters(chapters);
        }

        if (fields.contains("stats")) {
            long totalChapters = container.chapterSummaries != null ? container.chapterSummaries.size() : 0L;
            long totalLessons = container.chapterSummaries != null
                    ? container.chapterSummaries.stream().mapToLong(ChapterSummaryProjection::getTotalLessons).sum()
                    : 0L;

            CourseStats stats = CourseStats.builder()
                    .totalStudents(container.courseStats.getTotalStudents())
                    .rating(container.courseStats.getRating())
                    .totalReviews(container.courseStats.getTotalReviews())
                    .totalChapters(totalChapters)
                    .totalLessons(totalLessons)
                    .totalDuration(course.getTotalDuration()) .build();
            courseDetailResponse.setStats(stats);
        }

        if(fields.contains("instructor")){
            courseDetailResponse.setInstructor(container.instructorInfo != null
                    ? container.instructorInfo : null);
        }


        return courseDetailResponse;
    }
    @Data
    private static class DataContainer {
        private List<ChapterSummaryProjection> chapterSummaries;
        private Map<String, List<LessonSummaryProjection>> lessonsByChapter;
        private Set<String> completedLessons = Collections.emptySet();
        private CourseStatsProjection courseStats;
        private InstructorInfo instructorInfo;
    }

}

