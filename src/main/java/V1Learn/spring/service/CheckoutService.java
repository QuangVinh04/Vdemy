package V1Learn.spring.service;

import V1Learn.spring.dto.request.CheckoutCreationRequest;
import V1Learn.spring.dto.request.CheckoutItemRequest;
import V1Learn.spring.dto.response.CheckoutResponse;
import V1Learn.spring.entity.Checkout;
import V1Learn.spring.entity.CheckoutItem;
import V1Learn.spring.entity.Course;
import V1Learn.spring.enums.CheckoutState;
import V1Learn.spring.enums.CourseStatus;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.CheckoutMapper;
import V1Learn.spring.repository.CheckoutRepository;
import V1Learn.spring.repository.CourseAccessRepository;
import V1Learn.spring.repository.CourseRepository;
import V1Learn.spring.repository.EnrollmentRepository;
import V1Learn.spring.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutService {

    CourseService courseService;
    CourseAccessRepository courseAccessRepository;
    CourseRepository courseRepository;
    CheckoutRepository checkoutRepository;
    CheckoutMapper checkoutMapper;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public CheckoutResponse createCheckout(CheckoutCreationRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        // Expire checkout PENDING cũ của user
        expireOldPendingCheckouts(userId);

        Checkout checkout = checkoutMapper.toCheckout(request);
        checkout.setUserId(userId);
        checkout.setCheckoutState(CheckoutState.PENDING);

        prepareCheckoutItems(checkout, userId, request);
        checkout = checkoutRepository.save(checkout);

        log.info("Created checkout id: {} and userId: {} ", checkout.getId(), userId);

        return checkoutMapper.toCheckoutResponse(checkout);
    }

    private void prepareCheckoutItems(Checkout checkout, String userId, CheckoutCreationRequest request) {
        Set<String> courseIds = request.getItems().stream()
                .map(CheckoutItemRequest::getCourseId)
                .collect(Collectors.toSet());

        List<Course> courses = courseRepository.findBasicInfoByIds(courseIds);

        // Nếu số lượng khóa học tìm thấy ít hơn số lượng ID gửi lên -> Có ID ảo/không
        // tồn tại
        if (courses.size() != courseIds.size()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        Map<String, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, course -> course));

        // Gọi DB đúng 1 LẦN để lấy toàn bộ các khóa học user ĐÃ MUA trong danh sách này
        Set<String> accessibleCourseIds = courseAccessRepository.findValidAccessCourseIds(userId, courseIds);

        // Bắt đầu vòng lặp xử lý logic
        Set<CheckoutItem> enrichedItems = request.getItems().stream().map(requestItem -> {
            CheckoutItem item = checkoutMapper.toCheckoutItem(requestItem);

            // Trích xuất course từ Map đã lấy ở bước 2
            Course course = courseMap.get(item.getCourseId());

            // Kiểm tra nghiệp vụ
            if (course.getInstructor().getId().equals(userId)) {
                throw new AppException(ErrorCode.CANNOT_CHECKOUT_OWN_COURSE);
            }
            if (!CourseStatus.PUBLISHED.equals(course.getStatus())) {
                throw new AppException(ErrorCode.COURSE_NOT_AVAILABLE);
            }

            // Kiểm tra user đã mua chưa bằng Set lấy ở bước 3
            if (accessibleCourseIds.contains(course.getId())) {
                throw new AppException(ErrorCode.COURSE_ALREADY_OWNED);
            }

            // Gán dữ liệu
            item.setCheckout(checkout); // Đã loại bỏ peek() nguy hiểm
            item.setPrice(course.getPrice());
            item.setDiscountPrice(course.getDiscountPrice());

            return item;
        }).collect(Collectors.toSet());

        // Tính tổng tiền
        BigDecimal totalAmount = enrichedItems.stream()
                .map(item -> item.getDiscountPrice() != null ? item.getDiscountPrice() : item.getPrice()) // Ưu tiên giá
                                                                                                          // giảm
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        checkout.setItems(enrichedItems);
        checkout.setTotalAmount(totalAmount);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateCheckoutPaymentMethod(String checkoutId, String paymentMethod) {

        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new AppException(ErrorCode.CHECKOUT_NOT_FOUND));

        if (!checkout.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        if (checkout.getCheckoutState() != CheckoutState.PENDING) {
            throw new AppException(ErrorCode.CHECKOUT_INVALID_STATE);
        }

        checkout.setPaymentMethodId(paymentMethod);

        checkoutRepository.save(checkout);
    }

    @Transactional
    public void updateCheckoutState(String checkoutId, CheckoutState state) {
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new AppException(ErrorCode.CHECKOUT_NOT_FOUND));
        checkout.setCheckoutState(state);
        checkoutRepository.save(checkout);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public CheckoutResponse getCheckoutById(String checkoutId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new AppException(ErrorCode.CHECKOUT_NOT_FOUND));

        // Kiểm tra checkout có phải của user không
        if (!checkout.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        return checkoutMapper.toCheckoutResponse(checkout);
    }

    /**
     * Chỉ expire checkout PENDING cũ — nhẹ nhàng, không đụng order/payment
     * Checkout PAYMENT_PROCESSING sẽ do Scheduled Job xử lý
     */
    private void expireOldPendingCheckouts(String userId) {
        List<Checkout> pendingCheckouts = checkoutRepository
                .findByUserIdAndCheckoutState(userId, CheckoutState.PENDING);
        for (Checkout old : pendingCheckouts) {
            old.setCheckoutState(CheckoutState.EXPIRED);
            checkoutRepository.save(old);
            log.info("Expired old pending checkout: {}", old.getId());
        }
    }

}
