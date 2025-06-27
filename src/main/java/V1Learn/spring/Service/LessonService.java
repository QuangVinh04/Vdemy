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

import java.util.List;


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



    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public LessonResponse createLesson(LessonRequest request,
                                       MultipartFile lessonFile) throws AppException {
        log.info("Service: create Lesson");

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByChapterId(request.getChapterId()).orElse(0);

        Lesson lesson = Lesson.builder()
                .name(request.getName())
                .chapter(chapter)
                .contentType(request.getContentType())
                .description(request.getDescription())
                .orderIndex(maxOrderIndex + 1)
                .build();

        if(lessonFile != null) {
            String contentUrl = cloudinaryService.uploadLesson(lessonFile, request.getContentType());
            lesson.setContentUrl(contentUrl);
        }


        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }



    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public LessonResponse updateLesson(String lessonId,
                                       LessonRequest request,
                                       MultipartFile lessonFile) {
        log.info("Service: update Lesson");

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        if(lessonFile != null) {

            if(lesson.getContentUrl() != null) {
                cloudinaryService.deleteFile(lesson.getContentUrl(), lesson.getContentType());
            }
            lesson.setContentType(request.getContentType());
            String newContentUrl = cloudinaryService.uploadLesson(lessonFile, request.getContentType());
            lesson.setContentUrl(newContentUrl);
        }

        lessonRepository.save(lesson);

        return LessonResponse.builder()
                .name(lesson.getName())
                .description(lesson.getDescription())
                .contentUrl(lesson.getContentUrl())
                .build();
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @Transactional
    public void deleteLesson(String lessonId) throws AppException {
        log.info("Service: delete Lesson");
        lessonRepository.deleteById(lessonId);
    }

    public String getLessonContent(String lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Course course = lesson.getChapter().getCourse();

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean hasAccess = false;

        List<Enrollment> enrollmentList = enrollmentRepository.findByUserAndCourseId(user, course.getId());
        if(!enrollmentList.isEmpty()) {
            for(Enrollment e: enrollmentList) {
                if (e.getStatus().equals(EnrollmentStatus.COMPLETED) && e.getPayment().getStatus().equals(PaymentStatus.COMPLETED)) {
                    hasAccess = true;
                    break;
                }
            }
        }


        if (!hasAccess && !SecurityUtils.hasRole("ADMIN") && course.getInstructor().getId() != user.getId()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return lesson.getContentUrl();

    }
}
