package V1Learn.spring.service;

import V1Learn.spring.dto.request.ReplyReviewRequest;
import V1Learn.spring.dto.response.ReviewTeacherResponse;
import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.dto.event.ReviewRequest;
import V1Learn.spring.dto.response.AccessDecision;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.dto.response.ReviewResponse;
import V1Learn.spring.entity.Course;
import V1Learn.spring.entity.Review;
import V1Learn.spring.entity.User;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.ReviewMapper;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.ReviewRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.enums.NotificationType;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    KafkaTemplate<String, Object> kafkaTemplate;
    ReviewMapper reviewMapper;
    ReviewRepository reviewRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    CourseAccessService courseAccessService;

    public void sendReview(ReviewRequest request) {
        log.info("Sending review to Kafka: {}", request);
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Validate review request
        validateReviewRequest(request, user.getId());

        kafkaTemplate.send("course-review", user.getId(), request);
    }

    /**
     * Validate review request
     */
    private void validateReviewRequest(ReviewRequest request, String userId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 1. Kiểm tra user đã có quyền truy cập course (đã mua hoặc free)
        AccessDecision accessDecision = courseAccessService.checkUserAccess(course.getId(), userId);
        if (accessDecision.isHasFullAccess() && 
            course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        // 2. Kiểm tra rating hợp lệ (1-5)
        if (request.getRating() == null || 
            request.getRating() < 1 || 
            request.getRating() > 5) {
            throw new AppException(ErrorCode.REVIEW_INVALID_RATING);
        }

        // 3. Kiểm tra content không rỗng
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new AppException(ErrorCode.REVIEW_CONTENT_REQUIRED);
        }
    }

    @Transactional
    public void saveReview(ReviewRequest request, String userId) {
        log.info("Save review from Kafka: {}", request);
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Review review = Review.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .course(course)
                .user(user)
                .build();

        reviewRepository.save(review);

        NotificationEvent event = NotificationEvent.builder()
                        .recipientId(course.getInstructor().getId())
                        .title("Khóa học của bạn vừa được đánh giá")
                        .content(review.getContent())
                        .targetUrl(null)
                        .type(NotificationType.COURSE_REVIEW)
                        .createdAt(LocalDateTime.now())
                        .build();

        kafkaTemplate.send("notification-events", course.getInstructor().getId(), event);
    }

    public PageResponse<?> getReviewsByCourseWithSortBy(String courseId, int pageNo, int pageSize, String sortBy) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            // "createdAT:desc"
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String field = matcher.group(1);
                String direction = matcher.group(3);

                Sort.Order order = direction.equalsIgnoreCase("asc")
                        ? new Sort.Order(Sort.Direction.ASC, field)
                        : new Sort.Order(Sort.Direction.DESC, field);
                sorts.add(order);
            }
        }
        Pageable pageable = Sort.by(sorts).isSorted()
                ? PageRequest.of(page, pageSize, Sort.by(sorts))
                : PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAT")); // mặc định: mới nhất

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));


        Page<Review> reviewsPage = reviewRepository.findAllByCourseId(courseId, pageable);
        List<ReviewResponse> reviewResponses = reviewsPage.stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .userId(review.getUser().getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .createdAt(review.getCreatedAt())
                        .reviewerAvatar(review.getUser().getAvatar())
                        .reviewerName(review.getUser().getFullName())
                        .instructorName(course.getInstructor().getFullName())
                        .reply(review.getReply() != null ? review.getReply() : null)
                        .replyAt(review.getReply() != null ? review.getUpdatedAt() : null)
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(reviewsPage.getTotalPages())
                .items(reviewResponses)
                .build();
    }

    @Transactional
    public void updateReview(ReviewRequest request, String reviewId) {
        log.info("Updating review: {}", request);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));


        if (!Objects.equals(review.getUser().getId(), userId) || !Objects.equals(review.getCourse().getId(), course.getId())) {
            throw new AppException(ErrorCode.AUTH_FORBIDDEN);
        }

        reviewMapper.updateUser(review, request);
        reviewRepository.save(review);

    }

    @Transactional
    public void  deleteReview(String reviewId) {
        log.info("Deleting review: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));;
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        if (!Objects.equals(review.getUser().getId(), userId)) {
            throw new AppException(ErrorCode.AUTH_FORBIDDEN);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public PageResponse getReviewsByTeacher(Pageable pageable) {
        log.info("Get reviews by teacher");

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<Review> reviews = reviewRepository.findByInstructorId(userId, pageable);

        List<ReviewTeacherResponse> responses = reviews.stream().map(r -> {
            Course course = r.getCourse();
            User reviewer = r.getUser();

            return ReviewTeacherResponse.builder()
                    .id(r.getId())
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .reviewerName(reviewer.getFullName())
                    .reviewerAvatar(reviewer.getAvatar())
                    .content(r.getContent())
                    .rating(r.getRating())
                    .reply(r.getReply())
                    .createdAt(r.getCreatedAt().toString())
                    .build();
        }).toList();

        return PageResponse.builder()
                .pageNo(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalPage(reviews.getTotalPages())
                .items(responses)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public void replyToReview(String reviewId, ReplyReviewRequest request) {
        log.info("teacher reply to review: {}", request);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        Course course = review.getCourse();
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        if(!Objects.equals(course.getInstructor().getId(), userId)) {
            throw new AppException(ErrorCode.AUTH_FORBIDDEN);
        }

        review.setReply(request.getReplyContent());
        reviewRepository.save(review);

        NotificationEvent event = NotificationEvent.builder()
                .recipientId(review.getUser().getId())
                .title("Giảng viên đã phản hồi đánh giá")
                .content(request.getReplyContent())
                .targetUrl(null)
                .type(NotificationType.REVIEW_REPLY)
                .createdAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("notification-events", review.getUser().getId(), event);


    }



}
