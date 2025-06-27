package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.ChapterRequest;
import V1Learn.spring.DTO.Request.CourseCreationRequest;
import V1Learn.spring.DTO.Request.CourseUpdateRequest;
import V1Learn.spring.DTO.Request.LessonRequest;
import V1Learn.spring.DTO.Response.*;
import V1Learn.spring.Entity.*;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.ChapterMapper;
import V1Learn.spring.Mapper.CourseMapper;
import V1Learn.spring.Mapper.LessonMapper;
import V1Learn.spring.Repostiory.*;
import V1Learn.spring.Repostiory.specification.CourseSpecificationsBuilder;
import V1Learn.spring.utils.CourseMediaHelper;
import V1Learn.spring.utils.EnrollmentStatus;
import V1Learn.spring.utils.PaymentStatus;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    ChapterMapper chapterMapper;
    LessonMapper lessonMapper;
    CloudinaryService cloudinaryService;
    RedisService redisService;
    CourseMediaHelper courseMediaHelper;
    EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public CourseResponse createCourse(CourseCreationRequest request,
                                       MultipartFile thumbnail,
                                       MultipartFile introVideo,
                                       Map<String, MultipartFile> lessonVideos) {
        log.info("Service: create Course");

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User instructor = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Create the course
        Course course = courseMapper.toCourse(request);
        course.setInstructor(instructor);


        // Handle course thumbnail and video uploads
        if(thumbnail != null || introVideo != null) {
            courseMediaHelper.handleMediaUpload(course, thumbnail, introVideo, false);
        }

        // Create chapters and lessons
        Set<Chapter> chapters = new HashSet<>();
        if(request.getChapters() != null && !request.getChapters().isEmpty()) {
            int chapterIndex = 0;
            for (chapterIndex = 0; chapterIndex < request.getChapters().size(); chapterIndex++) {
                Chapter chapter = chapterMapper.toChapter(request.getChapters().get(chapterIndex));
                chapter.setCourse(course);
                chapter.setOrderIndex(chapterIndex);


                Set<Lesson> lessons = new HashSet<>();
                List<LessonRequest> lessonRequests = request.getChapters().get(chapterIndex).getLessons();
                int lessonIndex = 0;
                for (lessonIndex = 0; lessonIndex < lessonRequests.size(); lessonIndex++) {
                    Lesson lesson = lessonMapper.toLesson(lessonRequests.get(lessonIndex));
                    lesson.setOrderIndex(lessonIndex);

                    String videoKey = String.format("lessonVideos[%d][%d]", chapterIndex, lessonIndex);
                    MultipartFile lessonVideo = lessonVideos != null ? lessonVideos.get(videoKey) : null;
                    if(lessonVideo != null) {
                        String videoUrl = cloudinaryService.uploadLesson(lessonVideo, lessonRequests.get(lessonIndex).getContentType());
                        lesson.setContentUrl(videoUrl);
                    }
                    lesson.setChapter(chapter);
                    lessons.add(lesson);

                }
                chapter.setLessons(lessons);
                chapters.add(chapter);
            }
        }
        course.setChapters(chapters);
        courseRepository.save(course);
        CourseResponse courseResponse = courseMapper.toCourseResponse(course);
        courseResponse.setInstructorName(instructor.getFullName());
        courseResponse.setInstructorAvatar(instructor.getAvatar());

        return courseResponse;
    }



    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER') and isAuthenticated()")
    public CourseResponse updateCourse(String courseId,
                                       CourseUpdateRequest request,
                                       MultipartFile thumbnail,
                                       MultipartFile introVideo,
                                       Map<String, MultipartFile> lessonVideos) {
        log.info("Service: update Course");
        // 1. Lấy course hiện tại
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));


        // 3. Cập nhật các trường cơ bản
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setPrice(BigDecimal.valueOf(request.getPrice()));
        course.setLanguage(request.getLanguage());
        course.setStatus(request.getStatus());

        // 4. Xử lý thumbnail, intro video (nếu có)
        if (thumbnail != null || introVideo != null) {
            courseMediaHelper.handleMediaUpload(course, thumbnail, introVideo, true);
        }
        // 5. Xử lý chapters và lessons
        if (request.getChapters() != null) {
            Set<Chapter> existingChapters = course.getChapters();
            existingChapters.clear();

            for (int chapterIndex = 0; chapterIndex < request.getChapters().size(); chapterIndex++) {
                ChapterRequest chapterReq = request.getChapters().get(chapterIndex);
                Chapter chapter = chapterMapper.toChapter(chapterReq);
                chapter.setCourse(course);
                chapter.setOrderIndex(chapterIndex);

                Set<Lesson> lessons = new HashSet<>();
                List<LessonRequest> lessonRequests = chapterReq.getLessons();
                for (int lessonIndex = 0; lessonIndex < lessonRequests.size(); lessonIndex++) {
                    LessonRequest lessonReq = lessonRequests.get(lessonIndex);
                    Lesson lesson = lessonMapper.toLesson(lessonReq);
                    lesson.setOrderIndex(lessonIndex);

                    String videoKey = String.format("lessonVideos[%d][%d]", chapterIndex, lessonIndex);
                    MultipartFile lessonVideo = lessonVideos != null ? lessonVideos.get(videoKey) : null;
                    if (lessonVideo != null) {
                        log.info("video not null");
                        String videoUrl = cloudinaryService.uploadLesson(lessonVideo, lessonReq.getContentType());
                        lesson.setContentUrl(videoUrl);
                    }
                    lesson.setChapter(chapter);
                    lessons.add(lesson);
                }
                chapter.setLessons(lessons);
                existingChapters.add(chapter);
            }
        }

        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public void deleteCourse(String courseId) {
        log.info("Service: delete Course");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if(course.getThumbnailUrl() != null){
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(course.getThumbnailUrl()), "image");
        }
        if(course.getVideoUrl() != null){
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(course.getVideoUrl()), "video");
        }
        courseRepository.deleteById(courseId);
    }


    public CourseResponse getCourseDetail(String courseId) {
        log.info("Service: get Course Detail");

        Course course = courseRepository.findCourseWithChaptersAndLessons(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Optional<String> optionalUserId = SecurityUtils.getCurrentUserId();
        User user = null;
        List<Enrollment> enrollment;
        Payment payment = null;

        if (optionalUserId.isPresent()) {
            user = userRepository.findById(optionalUserId.get())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            enrollment = enrollmentRepository.findByUserAndCourseId(user, courseId);
            if (!enrollment.isEmpty()) {
               for(Enrollment e : enrollment){
                   if(e.getStatus().equals(EnrollmentStatus.COMPLETED)){
                       payment = e.getPayment();
                   }
               }
            }
        }

        CourseResponse courseResponse = courseMapper.toCourseResponse(course);
        courseResponse.setInstructorAvatar(course.getInstructor().getAvatar());
        courseResponse.setInstructorName(course.getInstructor().getFullName());

        boolean hasAccess = SecurityUtils.hasRole("ADMIN")
                || (user != null && course.getInstructor().getId().equals(user.getId()))
                || (payment != null && payment.getStatus().equals(PaymentStatus.COMPLETED))
                ||(user != null && course.getPrice().equals(BigDecimal.ZERO));

        courseResponse.setChapters(course.getChapters().stream()
                .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                .map(chapter -> ChapterResponse.builder()
                        .id(chapter.getId())
                        .title(chapter.getTitle())
                        .description(chapter.getDescription())
                        .lessons(chapter.getLessons().stream()
                                .sorted(Comparator.comparingInt(Lesson::getOrderIndex))
                                .map(lesson -> {
                                    if (hasAccess) {
                                        return lessonMapper.toLessonResponse(lesson);
                                    } else {
                                        return LessonResponse.builder()
                                                .id(lesson.getId())
                                                .name(lesson.getName())
                                                .description(lesson.getDescription())
                                                .contentUrl(null) // ẩn nội dung video
                                                .build();
                                    }
                                })
                                .collect(Collectors.toCollection(LinkedHashSet::new)))
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new)));

        return courseResponse;
    }





    public CourseResponse getCourseById(String courseID) {
        log.info("Service: get Course by id");
        Course course = courseRepository.findById(courseID)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        return courseMapper.toCourseResponse(course);
    }

    public PageResponse getAllCourses(Pageable pageable) {
        log.info("Service: getAllCourses");
        Page<Course> courses = courseRepository.findAll(pageable);
        return getPageResponse(pageable, courses);

    }
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public PageResponse getCourseByTeacher(Pageable pageable) {
        log.info("Service: get course by teacher");
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<Course> courses = courseRepository.findCourseByInstructorId(userId, pageable);

        List<CourseTeacherResponse> responses = courses.stream().map(c -> {
                    CourseTeacherResponse response = courseMapper.toCourseTeacherResponse(c);
                    response.setInstructorName(c.getInstructor().getFullName());
                    response.setInstructorAvatar(c.getInstructor().getAvatar());
                    response.setUserEnrolled(enrollmentRepository.countByCourseId(c.getId(), EnrollmentStatus.COMPLETED));
                    return response;
                })
                .toList();
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(courses.getTotalPages())
                .items(responses)
                .build();

    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public PageResponse getCourseByUser(Pageable pageable) {
        log.info("Service: get course by user");
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<Course> courses = enrollmentRepository.findSuccessfulCoursesByUserId(userId, EnrollmentStatus.COMPLETED, pageable);
        return getPageResponse(pageable, courses);

    }




    public PageResponse advanceSearchWithSpecifications(Pageable pageable, String[] course) {
        log.info("Service: search course by specifications");

         if (course != null) {

            CourseSpecificationsBuilder builder = new CourseSpecificationsBuilder();

            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(\\p{Punct}?)(.*)(\\p{Punct}?)");
            for (String s : course) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
                }
            }
            // builder.build(): trả về Specification<User> dựa trên các điều kiện tìm kiếm đã được thêm vào builder trước đó.
            Page<Course> courses = courseRepository.findAll(Objects.requireNonNull(builder.build()), pageable);
             return getPageResponse(pageable, courses);
         }

        Page<Course> courses = courseRepository.findAll(pageable);
        return getPageResponse(pageable, courses);

    }

    private PageResponse getPageResponse(Pageable pageable, Page<Course> courses) {
        List<CourseResponse> responses = courses.stream().map(c -> {
                    CourseResponse response = courseMapper.toCourseResponse(c);
                    response.setInstructorName(c.getInstructor().getFullName());
                    response.setInstructorAvatar(c.getInstructor().getAvatar());
                    return response;
                })
                .toList();
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(courses.getTotalPages())
                .items(responses)
                .build();
    }


}
