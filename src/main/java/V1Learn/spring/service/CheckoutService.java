package V1Learn.spring.service;

import V1Learn.spring.dto.request.CheckoutCreationRequest;
import V1Learn.spring.dto.response.CheckoutResponse;
import V1Learn.spring.entity.Checkout;
import V1Learn.spring.entity.CheckoutItem;
import V1Learn.spring.entity.Course;
import V1Learn.spring.enums.CheckoutState;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.CheckoutMapper;
import V1Learn.spring.repository.CheckoutRepository;
import V1Learn.spring.repository.CourseRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutService {

    CourseService courseService;
    CourseRepository courseRepository;
    CheckoutRepository checkoutRepository;
    CheckoutMapper checkoutMapper;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public CheckoutResponse createCheckout(CheckoutCreationRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Checkout checkout = checkoutMapper.toCheckout(request);
        checkout.setUserId(userId);
        checkout.setCheckoutState(CheckoutState.PENDING);

        prepareCheckoutItems(checkout, request);
        checkout = checkoutRepository.save(checkout);

        log.info("Created checkout id: {} and userId: {} ", checkout.getId(), userId);

        return checkoutMapper.toCheckoutResponse(checkout);
    }

    private void prepareCheckoutItems(Checkout checkout, CheckoutCreationRequest request) {
        List<CheckoutItem> checkoutItems = request.getItems()
                .stream()
                .map(checkoutMapper::toCheckoutItem)
                .peek(item -> item.setCheckout(checkout)).toList();

        Set<CheckoutItem> enrichedItems = checkoutItems.stream().map(item -> {
            Course course = courseRepository.findById(item.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

            return CheckoutItem.builder()
                    .checkout(checkout)
                    .courseId(item.getCourseId())
                    .price(course.getPrice())
                    .discountPrice(course.getDiscountPrice())
                    .build();
        }).collect(Collectors.toSet());

        BigDecimal totalAmount = enrichedItems.stream()
                .map(CheckoutItem::getPrice)
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

}
