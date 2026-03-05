package V1Learn.spring.controller;

import V1Learn.spring.dto.request.ReplyReviewRequest;
import V1Learn.spring.dto.event.ReviewRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/review")
@Slf4j
public class ReviewController {
    ReviewService reviewService;

    @PostMapping()
    APIResponse<?> createReview(@RequestBody @Valid ReviewRequest reviewRequest) {
        log.info("Creating a review for course [{}]", reviewRequest.getCourseId());
        reviewService.sendReview(reviewRequest);
        return APIResponse.builder()
                .result("Review pending ...")
                .build();
    }


    @GetMapping("/course-review/{courseId}")
    public APIResponse<PageResponse> getReviews(@PathVariable("courseId") String courseId,
                                                @RequestParam(defaultValue = "0") int pageNo,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                @RequestParam(required = false) String orderBy) {
        log.info("Fetching reviews for course [{}], page [{}], orderBy [{}]", courseId, pageNo, orderBy);
        return APIResponse.<PageResponse>builder()
                .result(reviewService.getReviewsByCourseWithSortBy(courseId, pageNo, pageSize, orderBy))
                .build();
    }

    @GetMapping("/teacher/reviews")
    public APIResponse<?> getCoursesByTeacher(
            @PageableDefault(page = 0, size = 10, sort = "createdAT", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Fetching courses by logged-in teacher");
        return APIResponse.builder()
                .result(reviewService.getReviewsByTeacher(pageable))
                .build();
    }

    @PutMapping("/{reviewId}")
    public APIResponse<?> update(@PathVariable("reviewId") String reviewId,
                                @RequestBody @Valid ReviewRequest request) {
        log.info(" updating review [{}]", reviewId);
        reviewService.updateReview(request, reviewId);
        return APIResponse.builder()
                .result("Successful review update")
                .build();
    }

    @DeleteMapping("/{reviewId}")
    public APIResponse<?> delete(@PathVariable("reviewId") String reviewId) {
        log.info("deleting review [{}]", reviewId);
        reviewService.deleteReview(reviewId);
        return APIResponse.builder()
                .result("Review removed successfully")
                .build();
    }

    @PutMapping("/{reviewId}/reply")
    public APIResponse<?> reply(@PathVariable("reviewId") String reviewId,
                                 @RequestBody ReplyReviewRequest request) {
        log.info("Teacher  is replying to review [{}]", reviewId);
        reviewService.replyToReview(reviewId, request);
        return APIResponse.builder()
                .result("Successful review response")
                .build();
    }
}