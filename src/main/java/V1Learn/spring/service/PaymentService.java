package V1Learn.spring.service;


import V1Learn.spring.dto.event.PaymentResultEvent;
import V1Learn.spring.dto.request.InitPaymentRequest;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.dto.response.PaymentResponse;
import V1Learn.spring.dto.event.NotificationEvent;
import V1Learn.spring.entity.*;
import V1Learn.spring.enums.*;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.model.payment.CapturedPayment;
import V1Learn.spring.model.payment.InitiatedPayment;
import V1Learn.spring.repository.*;
import V1Learn.spring.service.handler.PaymentHandler;
import V1Learn.spring.utils.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    EnrollmentRepository enrollmentRepository;
    PaymentRepository paymentRepository;
    CheckoutRepository checkoutRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    CheckoutService checkoutService;
    OrderService orderService;
    CourseAccessService courseAccessService;
    EnrollmentService enrollmentService;
    CartService cartService;

    List<PaymentHandler> handlers;
    Map<String, PaymentHandler> providerMap = new HashMap<>();

    final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        for (PaymentHandler handler : handlers) {
            providerMap.put(handler.getProviderName(), handler);
        }
    }

    public PaymentHandler getHandler(String providerName) {
        PaymentHandler handler = providerMap.get(providerName.toLowerCase());
        if (handler == null) {
            throw new RuntimeException("No handler found for: " + providerName);
        }
        return handler;
    }



    @Transactional
    public PaymentResponse createPayment(InitPaymentRequest paymentRequest, HttpServletRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Checkout checkout = checkoutRepository.findById(paymentRequest.getCheckoutId())
                .orElseThrow(() -> new AppException(ErrorCode.CHECKOUT_NOT_FOUND));

        // Kiểm tra checkout có phải của người dùng không
        if (!checkout.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        // Cập nhật phương thức thanh toán vào checkout
        checkoutService.updateCheckoutPaymentMethod(paymentRequest.getCheckoutId(),
                paymentRequest.getPaymentMethod());

        // 2. TẠO ORDER TỪ CHECKOUT
        Order order = orderService.createOrderFromCheckout(paymentRequest.getCheckoutId());

        Payment payment = Payment.builder()
                .paymentMethod(PaymentMethod.valueOf(paymentRequest.getPaymentMethod()))
                .amount(checkout.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .order(order)
                .build();

        paymentRepository.save(payment);

        // Lấy handler tương ứng
        PaymentHandler handler = getHandler(paymentRequest.getPaymentMethod());

        // Tạo paymentUrl
        InitiatedPayment initiated = handler.initPayment(payment, request);

        return PaymentResponse.builder()
                .orderCode(order.getOrderCode())
                .message("Payment created")
                .paymentId(payment.getTransactionId())
                .paymentUrl(initiated.getRedirectUrl())
                .build();


    }

    @Transactional
    public void handleCallback(String provider, Map<String, String> params) {

        PaymentHandler handler = getHandler(provider);
        CapturedPayment captured = handler.handleCallback(params);


        Payment payment = paymentRepository
                .findByTransactionId(captured.getTransactionId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        // chống callback trùng
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Payment {} already completed", payment.getTransactionId());
            return;
        }

        // verify amount
        if (payment.getAmount().compareTo(captured.getAmount()) != 0) {
            throw new AppException(ErrorCode.AMOUNT_MISMATCH);
        }

        payment.setStatus(captured.getPaymentStatus());
        payment.setGatewayTransactionId(captured.getGatewayTransactionId());
        payment.setGatewayRawResponse(captured.getRawResponse());
        paymentRepository.save(payment);


        Order order = payment.getOrder();

        if (captured.getPaymentStatus().equals(PaymentStatus.COMPLETED)) {

            order.setStatus(OrderStatus.COMPLETED);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);

            checkoutService.updateCheckoutState(order.getCheckout().getId(), CheckoutState.COMPLETED);

            User user = order.getUser();

            for (OrderItem item : order.getOrderItems()) {
                Course course = courseRepository.findById(item.getCourseId())
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

                courseAccessService.grantAccess(course.getId(), user.getId(), AccessType.PURCHASED);

                enrollmentService.ensureEnrollment(user, course);

            }

            // Xóa cart items sau khi payment thành công
            cartService.clearCartAfterPayment(user.getId(), order.getOrderItems());

            notifyPaymentResult(user.getId(),
                    new PaymentResultEvent(
                            payment.getTransactionId(),
                            "SUCCESS"
                    )
            );


            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(user.getId())
                    .title("Thanh toán thành công")
                    .content("Bạn đã mua khóa học thành công")
                    .type(NotificationType.ORDER_CONFIRMED)
                    .createdAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("notification-events", user.getId(), event);
        }

        else{
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            checkoutService.updateCheckoutState(order.getCheckout().getId(), CheckoutState.PAYMENT_FAILED);

            User user = order.getUser();

            notifyPaymentResult(user.getId(),
                    new PaymentResultEvent(
                            payment.getTransactionId(),
                            "FAILED"
                    )
            );
        }
    }
    public void notifyPaymentResult(String userId, PaymentResultEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/payment/" + userId,
                event
        );
    }

    
@Transactional(readOnly = true)
@PreAuthorize("isAuthenticated()")
public PaymentResponse getPaymentStatus(String paymentId) {
    String userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
    
    Payment payment = paymentRepository.findByTransactionId(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    
    // Kiểm tra payment có phải của user không
    if (!payment.getOrder().getUser().getId().equals(userId)) {
        throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
    }
    
    return PaymentResponse.builder()
            .paymentId(payment.getTransactionId())
            .status(payment.getStatus())
            .amount(payment.getAmount())
            .orderCode(payment.getOrder().getOrderCode())
            .createdAt(payment.getCreatedAt())
            .build();
}

@Transactional(readOnly = true)
@PreAuthorize("isAuthenticated()")
public PageResponse<?> getPaymentHistory(Pageable pageable) {
    String userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
    
    Page<Payment> payments = paymentRepository.findByOrderUserIdOrderByCreatedAtDesc(userId, pageable);
    
    List<PaymentResponse> responses = payments.stream()
            .map(payment -> PaymentResponse.builder()
                    .id(payment.getTransactionId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .paymentMethod(payment.getPaymentMethod())
                    .orderCode(payment.getOrder().getOrderCode())
                    .createdAt(payment.getCreatedAt())
                    .build())
            .toList();
    
    return PageResponse.builder()
            .pageNo(pageable.getPageNumber())
            .pageSize(pageable.getPageSize())
            .totalPage(payments.getTotalPages())
            .items(responses)
            .build();
}




//    public void cancelEnrollment(String enrollmentId) {
//        String userId = SecurityUtils.getCurrentUserId()
//                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
//                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));
//
//        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
//            throw new AppException(ErrorCode.ENROLLMENT_ALREADY_COMPLETED);
//        }
//
//        if(!enrollment.getUser().getId().equals(user.getId())){
//            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
//        }
//
//        enrollment.setStatus(EnrollmentStatus.CANCELLED);
//
//        Payment payment = enrollment.getPayment();
//        if (payment != null) {
//            payment.setStatus(PaymentStatus.FAILED);
//            paymentRepository.save(payment);
//        }
//
//        enrollmentRepository.save(enrollment);
//    }

}
