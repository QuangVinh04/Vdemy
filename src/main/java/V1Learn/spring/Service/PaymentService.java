package V1Learn.spring.Service;


import V1Learn.spring.DTO.Request.PaymentRequest;
import V1Learn.spring.DTO.Response.PaymentResponse;
import V1Learn.spring.DTO.event.NotificationEvent;
import V1Learn.spring.Entity.Course;
import V1Learn.spring.Entity.Enrollment;
import V1Learn.spring.Entity.Payment;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Repostiory.CourseRepository;
import V1Learn.spring.Repostiory.EnrollmentRepository;
import V1Learn.spring.Repostiory.PaymentRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    VNPayService vNPayService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, HttpServletRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(paymentRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();


        Payment payment = Payment.builder()
                .enrollment(enrollment)
                .provider(paymentRequest.getPaymentProvider())
                .amount(course.getPrice())
                .status(PaymentStatus.PENDING)
                .build();
        enrollment.setPayment(payment);

        enrollmentRepository.save(enrollment);
        paymentRepository.save(payment);


        return PaymentResponse.builder()
                .code("ok")
                .message("Payment created")
                .paymentUrl(vNPayService.createVNPayPayment(enrollment, request))
                .build();


    }

    public void handleCallback(Map<String, String> params, PaymentProvider provider) {
        if(provider != PaymentProvider.VN_PAY){
            throw new RuntimeException("Invalid payment");
        }
        if(!vNPayService.verifyPayment(params)){
            throw new RuntimeException("Invalid payment");
        }
        String enrollmentId = params.get("vnp_TxnRef").split("_")[1];
        log.info(enrollmentId);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));
        Payment payment = enrollment.getPayment();

        String vnpAmountStr = params.get("vnp_Amount"); // ví dụ: "12000000"
        BigDecimal vnpAmount = new BigDecimal(vnpAmountStr);

        // Lấy số tiền trong hệ thống và nhân 100 để đối chiếu với VNPay
        BigDecimal expectedAmount = payment.getAmount().multiply(BigDecimal.valueOf(25000)).multiply(BigDecimal.valueOf(100));

        log.info("VNPay amount: {}, Expected amount: {}", vnpAmount, expectedAmount);

        if (vnpAmount.compareTo(expectedAmount) != 0) {
            throw new RuntimeException("Mismatch amount in callback. Possible tampering.");
        }

        String responseCode = params.get("vnp_ResponseCode");
        if(responseCode.equals("00")){
            payment.setStatus(PaymentStatus.COMPLETED);
            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(enrollment.getUser().getId())
                    .title("Đơn hàng đã được xác nhận")
                    .content("Khóa học " + enrollment.getCourse().getTitle() + " đã được thanh toán thành công.")
                    .targetUrl(null)
                    .type(NotificationType.ORDER_CONFIRMED)
                    .createdAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("notification-events", enrollment.getUser().getId(), event);
        }
        else{
            payment.setStatus(PaymentStatus.FAILED);
            enrollment.setStatus(EnrollmentStatus.CANCELLED);
        }
        paymentRepository.save(payment);
        enrollmentRepository.save(enrollment);
    }


    public void cancelEnrollment(String enrollmentId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXIST));

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.ENROLLMENT_ALREADY_COMPLETED);
        }

        if(!enrollment.getUser().getId().equals(user.getId())){
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        Payment payment = enrollment.getPayment();
        if (payment != null) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        enrollmentRepository.save(enrollment);
    }

}
