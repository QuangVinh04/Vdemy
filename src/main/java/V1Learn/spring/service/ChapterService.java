package V1Learn.spring.service;


import V1Learn.spring.dto.request.ChapterRequest;
import V1Learn.spring.dto.response.ChapterResponse;
import V1Learn.spring.entity.Chapter;
import V1Learn.spring.entity.Course;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.ChapterMapper;
import V1Learn.spring.repository.ChapterRepository;
import V1Learn.spring.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterService {

    CourseRepository courseRepository;
    ChapterMapper chapterMapper;
    ChapterRepository chapterRepository;


    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ChapterResponse createChapter(String courseId, ChapterRequest request) throws AppException {
        log.info("Service: create Chapter");

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Lấy orderIndex lớn nhất hiện có, nếu chưa có Chapter nào thì gán = 0
        Integer maxOrderIndex = chapterRepository.findMaxOrderIndexByCourseId(courseId).orElse(0);

        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setOrderIndex(maxOrderIndex + 1);
        chapter.setCourse(course);
        return chapterMapper.toChapterResponse(chapterRepository.save(chapter));
    }



    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public void updateChapter(String chapterId, ChapterRequest request) {
        log.info("Service: update Chapter");

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        chapter.setTitle(request.getTitle());
        chapter.setDescription(request.getDescription());
        chapterRepository.save(chapter);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @Transactional
    public void deleteChapter(String chapterId) throws AppException {
        log.info("Service: delete Chapter");
        chapterRepository.deleteById(chapterId);
    }



    public List<ChapterResponse> getChaptersByCourseId(String courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseId(courseId);
        return chapters.stream()
                .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                .map(chapter -> ChapterResponse.builder()
                        .id(chapter.getId())
                        .title(chapter.getTitle())
                        .description(chapter.getDescription())
                        .build())
                .collect(Collectors.toList());
    }



}
