package V1Learn.spring.service;

import V1Learn.spring.dto.request.LessonRequest;
import V1Learn.spring.dto.response.AccessDecision;
import V1Learn.spring.dto.response.LessonResponse;
import V1Learn.spring.entity.*;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.LessonMapper;
import V1Learn.spring.repository.ChapterRepository;
import V1Learn.spring.repository.LessonRepository;
import V1Learn.spring.projection.LessonSummaryProjection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {

    LessonRepository lessonRepository;
    ChapterRepository chapterRepository;
    LessonMapper lessonMapper;
    CourseCacheService courseCacheService;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public LessonResponse createLesson(String chapterId,
            LessonRequest request) throws AppException {

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByChapterId(chapterId).orElse(0);

        Lesson lesson = lessonMapper.toLesson(request);
        lesson.setChapter(chapter);
        lesson.setOrderIndex(maxOrderIndex + 1);

        LessonResponse response = lessonMapper.toLessonResponseBase(lessonRepository.save(lesson));
        courseCacheService.evictCourseContentCache(chapter.getCourse().getId());
        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public LessonResponse updateLesson(String lessonId,
            LessonRequest request) throws AppException {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        lessonMapper.updateLesson(lesson, request);

        LessonResponse response = lessonMapper.toLessonResponseBase(lessonRepository.save(lesson));
        courseCacheService.evictCourseContentCache(lesson.getChapter().getCourse().getId());
        return response;
    }

    public LessonResponse buildLesson(LessonSummaryProjection lessonSummaries,
            AccessDecision accessDecision,
            Set<String> completedLessons) {

        boolean isCompleted = completedLessons.contains(lessonSummaries.id());
        boolean isPreview = Boolean.TRUE.equals(lessonSummaries.isPreview());
        boolean canSeeMedia = accessDecision.isHasFullAccess() || isPreview;

        LessonResponse response = lessonMapper.toLessonResponse(lessonSummaries);
        if (canSeeMedia) {
            response.setFileUrl(lessonSummaries.fileUrl());
            response.setVideoUrl(lessonSummaries.videoUrl());
            response.setIsCompleted(isCompleted);
        }
        response.setIsLocked(!canSeeMedia);
        return response;

    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @Transactional
    public void deleteLesson(String lessonId) throws AppException {
        log.info("Service: delete Lesson");
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        String courseId = lesson.getChapter().getCourse().getId();
        lessonRepository.delete(lesson);
        courseCacheService.evictCourseContentCache(courseId);
    }

}
