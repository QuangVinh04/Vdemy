package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.*;
import V1Learn.spring.Entity.*;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.ChapterMapper;
import V1Learn.spring.Mapper.CourseMapper;
import V1Learn.spring.Mapper.LessonMapper;
import V1Learn.spring.Repostiory.*;
import V1Learn.spring.Repostiory.specification.CourseSpecificationsBuilder;
import V1Learn.spring.enums.CourseStatus;
import V1Learn.spring.enums.EnrollmentStatus;
import V1Learn.spring.utils.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    EnrollmentRepository enrollmentRepository;
    CategoryRepository categoryRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER') and isAuthenticated()")
    public CourseResponse createNewCourse(CourseCreationRequest request) {

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User instructor = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = new Course();
        course.setInstructor(instructor);
        course.setStatus(CourseStatus.DRAFT);
        Category category = categoryRepository.findFirstByOrderByCreatedAtAsc()
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        course.setCategory(category);

        if (request.getTempTitle() != null && !request.getTempTitle().isEmpty()) {
            course.setTitle(request.getTempTitle());
        } else {
            course.setTitle("Khóa học chưa đặt tên");
        }
        course = courseRepository.save(course);
        log.info("Created draft course with ID: {} for instructor: {}", course.getId(), userId);
        return courseMapper.toCourseResponse(course);
    }




    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER') and isAuthenticated()")
    public CourseResponse updateCourse(String courseId,
                                       CourseUpdateRequest request) {

        Course course = getOwnedCourse(courseId);
        courseMapper.updateCourse(course, request);
        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        course.setCategory(category);
        category.getCourses().add(course);

        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public void publish(String courseId) throws AppException {
        log.info("Service: Publish Course for courseId={}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));


        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_ALREADY_PUBLISHED);
        }
        course.setStatus(CourseStatus.PUBLISHED);
        courseMapper.toCourseResponse(courseRepository.save(course));
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public void deleteCourse(String courseId) {
        log.info("Service: delete Course");
        courseRepository.deleteById(courseId);
    }


    public CourseResponse getBasicCourseInfo(String courseId){
        log.info("Service: get BasicCourseInfo");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        CourseResponse response = courseMapper.toCourseResponse(course);
        response.setInstructorName(course.getInstructor().getFullName());
        response.setInstructorAvatar(course.getInstructor().getAvatar());
        return response;
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


    private Course getOwnedCourse(String courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        String uid = SecurityUtils.getCurrentUserId().orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        if (!Objects.equals(uid, course.getInstructor().getId())) throw new AppException(ErrorCode.AUTH_FORBIDDEN);
        return course;
    }


}
