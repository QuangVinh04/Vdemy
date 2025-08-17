package V1Learn.spring.Service;

import V1Learn.spring.DTO.Request.LessonRequest;
import V1Learn.spring.DTO.Response.LessonResponse;
import V1Learn.spring.Entity.*;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.LessonMapper;
import V1Learn.spring.Repostiory.ChapterRepository;
import V1Learn.spring.Repostiory.EnrollmentRepository;
import V1Learn.spring.Repostiory.LessonRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.utils.EnrollmentStatus;
import V1Learn.spring.utils.PaymentStatus;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {

    LessonRepository lessonRepository;
    UserRepository userRepository;
    ChapterRepository chapterRepository;
    EnrollmentRepository enrollmentRepository;
    CloudinaryService cloudinaryService;
    LessonMapper lessonMapper;


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public LessonResponse createLesson(String chapterId,
                                       LessonRequest request) throws AppException {


        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByChapterId(chapterId).orElse(0);

        Lesson lesson = lessonMapper.toLesson(request);
        lesson.setChapter(chapter);
        lesson.setOrderIndex(maxOrderIndex + 1);

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }




    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @Transactional
    public void deleteLesson(String lessonId) throws AppException {
        log.info("Service: delete Lesson");
        lessonRepository.deleteById(lessonId);
    }



}
