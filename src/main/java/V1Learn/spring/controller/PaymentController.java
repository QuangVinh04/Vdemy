package V1Learn.spring.controller;

import V1Learn.spring.DTO.Request.CourseCreationRequest;
import V1Learn.spring.DTO.Request.CourseUpdateRequest;
import V1Learn.spring.DTO.Request.PaymentRequest;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.PageResponse;
import V1Learn.spring.Service.CourseDraftService;
import V1Learn.spring.Service.CourseService;
import V1Learn.spring.Service.PaymentService;
import V1Learn.spring.utils.PaymentProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/payment")
@Slf4j
public class PaymentController {
    PaymentService paymentService;

    @PostMapping("/vn-pay")
    APIResponse<?> createVNPay(@RequestBody PaymentRequest paymentRequest,
                             HttpServletRequest request) {
        log.info("Controller: create Payment");
        return APIResponse.builder()
                .result(paymentService.createPayment(paymentRequest, request))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    APIResponse<?> handleVNPayCallback(@RequestParam Map<String, String> params) {
        log.info("Controller: handleVNPayCallback");
        paymentService.handleCallback(params, PaymentProvider.VN_PAY);
        return APIResponse.builder()
                .result("Thanh toán thành công")
                .build();
    }

    @PostMapping("/cancel")
    public APIResponse<String> cancelEnrollment(@RequestParam String courseId) {
        log.info("Controller: cancelEnrollment");
        paymentService.cancelEnrollment(courseId);
        return APIResponse.<String>builder()
                .result("Hủy đăng ký thành công")
                .build();
    }

}
