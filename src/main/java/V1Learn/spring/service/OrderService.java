package V1Learn.spring.service;

import V1Learn.spring.dto.response.OrderResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.entity.*;
import V1Learn.spring.enums.CheckoutState;
import V1Learn.spring.enums.CourseStatus;
import V1Learn.spring.enums.OrderStatus;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.mapper.OrderMapper;
import V1Learn.spring.repository.*;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    CheckoutRepository checkoutRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    OrderMapper orderMapper;
    CourseAccessService courseAccessService;
    CheckoutService checkoutService;

    @Transactional
    public Order createOrderFromCheckout(String checkoutId) {
        log.info("Creating order for checkoutId: {}", checkoutId);

        // Validate checkout exists
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new AppException(ErrorCode.CHECKOUT_NOT_FOUND));

        // Validate checkout state
        if (checkout.getCheckoutState() != CheckoutState.PENDING) {
            throw new AppException(ErrorCode.CHECKOUT_ALREADY_USED);
        }

        // Validate payment method
        if (checkout.getPaymentMethodId() == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // Validate user
        User user = userRepository.findById(checkout.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        validateCoursesForOrder(checkout, user.getId());

        // Create order with unique code
        String orderCode = generateOrderCode();
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(checkout.getTotalAmount())
                .discountAmount(checkout.getTotalDiscountAmount())
                .finalAmount(checkout.getTotalAmount().subtract(checkout.getTotalDiscountAmount()))
                .orderCode(orderCode)
                .checkout(checkout)
                .build();

        // Create order items
        Set<OrderItem> orderItems = checkout.getItems().stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .courseId(item.getCourseId())
                        .price(item.getPrice())
                        .discountPrice(item.getDiscountPrice())
                        .build())
                .collect(Collectors.toSet());

        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        checkoutService.updateCheckoutState(checkoutId, CheckoutState.PAYMENT_PROCESSING);

        log.info("Successfully created order: {} for user: {}",
                orderCode, user.getEmail());

        return orderRepository.save(order);
    }

    /**
     * Validate courses
     */
    private void validateCoursesForOrder(Checkout checkout, String userId) {
        if (checkout.getItems() == null || checkout.getItems().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        for (CheckoutItem item : checkout.getItems()) {
            Course course = courseRepository.findById(item.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

            // 1. Kiểm tra course status
            if (course.getStatus() != CourseStatus.PUBLISHED) {
                throw new AppException(ErrorCode.COURSE_NOT_AVAILABLE);
            }

            // 2. Kiểm tra user đã sở hữu course chưa
            if (courseAccessService.hasPurchasedAccess(userId, course.getId())) {
                throw new AppException(ErrorCode.COURSE_ALREADY_OWNED);
            }

            // 3. Kiểm tra user không phải là instructor
            if (course.getInstructor().getId().equals(userId)) {
                throw new AppException(ErrorCode.CANNOT_CHECKOUT_OWN_COURSE);
            }
        }
    }

    private String generateOrderCode() {
        return String.format("ORD-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public PageResponse<?> getMyOrders(Pageable pageable) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<OrderResponse> responses = orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(orders.getTotalPages())
                .items(responses)
                .build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public OrderResponse getOrderById(String orderId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra order có phải của user không
        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void cancelOrder(String orderId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra order có phải của user không
        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        // Chỉ cho phép hủy khi order chưa thanh toán
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("Order {} cancelled by user {}", orderId, userId);
    }

}
