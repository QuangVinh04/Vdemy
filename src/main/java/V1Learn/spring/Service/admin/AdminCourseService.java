package V1Learn.spring.Service.admin;

import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.DTO.Response.admin.AdminCourseResponse;

import V1Learn.spring.Entity.Course;

import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Mapper.admin.AdminCourseMapper;
import V1Learn.spring.Repostiory.CourseRepository;
import V1Learn.spring.enums.CourseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service handles operations related to course management in the system.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCourseService {

    CourseRepository courseRepository;
    AdminCourseMapper courseMapper;

    /**
     * Retrieve all courses with pagination.
     *
     * @param pageable pagination object (page number, size, sort)
     * @return PageResponse object contains list of courses and pagination information
     */
    public PageResponse<List<AdminCourseResponse>> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return getPageResponse(pageable, courses);
    }


    /**
     * Bans a course by setting its status to BANNED.
     *
     * @param courseId the ID of the course to ban
     * @throws AppException if the course is not found or is already banned
     */
    @Transactional
    public void banCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus().equals(CourseStatus.BANNED)) {
            throw new AppException(ErrorCode.COURSE_ALREADY_BANNED);
        }

        course.setStatus(CourseStatus.BANNED);
        courseRepository.save(course);
    }

    /**
     * Unbans a previously banned course, changing its status to PUBLISHED.
     *
     * @param courseId the ID of the course to unban
     * @throws AppException if the course is not found or is not currently banned
     */
    @Transactional
    public void unbanCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getStatus().equals(CourseStatus.BANNED)) {
            throw new AppException(ErrorCode.COURSE_NOT_BANNED);
        }

        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
    }


    /**
     * Retrieves all banned courses with pagination.
     *
     * @param pageable pagination information
     * @return a paginated response containing only banned courses
     */
    public PageResponse<List<AdminCourseResponse>> getBannedCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findByStatus(pageable, CourseStatus.BANNED);
        return getPageResponse(pageable, courses);
    }

    /**
     * Retrieves all active (PUBLISHED or PRIVATE) courses with pagination.
     *
     * @param pageable pagination information
     * @return a paginated response containing active courses
     */
    public PageResponse<List<AdminCourseResponse>> getActiveCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findByStatusEnabled(pageable, CourseStatus.PUBLISHED, CourseStatus.PRIVATE);
        return getPageResponse(pageable, courses);
    }


    /**
     * Retrieves detailed information about a specific course.
     *
     * @param id the ID of the course
     * @return detailed course response
     * @throws AppException if the course is not found
     */
    public AdminCourseResponse getCourseDetails(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        AdminCourseResponse response = courseMapper.toCourseResponse(course);
        response.setAuthorId(course.getInstructor() != null ? course.getInstructor().getId() : null);
        response.setAuthorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null);
        response.setEnabled(!course.getStatus().equals(CourseStatus.BANNED));

        return response;
    }

    /**
     * Converts a page of Course entities into a standardized PageResponse with mapped DTOs.
     *
     * @param pageable pagination info
     * @param courses  paged course entities
     * @return PageResponse with mapped AdminCourseResponse list
     */
    private PageResponse<List<AdminCourseResponse>> getPageResponse(Pageable pageable, Page<Course> courses) {
        List<AdminCourseResponse> responses = courses.stream()
                .map(c -> {
                    AdminCourseResponse courseResponse = courseMapper.toCourseResponse(c);
                    courseResponse.setAuthorId(c.getInstructor().getId());
                    courseResponse.setAuthorName(c.getInstructor().getFullName());
                    return courseResponse;
                }).toList();

        return PageResponse.<List<AdminCourseResponse>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(courses.getTotalPages())
                .items(responses)
                .build();
    }

}


