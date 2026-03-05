package V1Learn.spring.controller;


import V1Learn.spring.dto.request.InitPaymentRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.dto.response.PaymentResponse;
import V1Learn.spring.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/payment")
@Slf4j
public class PaymentController {
    PaymentService paymentService;

    /**
     * Khởi tạo payment từ checkout
     */
    @PostMapping("/init")
    public APIResponse<PaymentResponse> initPayment(
            @RequestBody @Valid InitPaymentRequest paymentRequest,
            HttpServletRequest request) {
        log.info("Request create Payment for checkout: {}", paymentRequest.getCheckoutId());
        PaymentResponse result = paymentService.createPayment(paymentRequest, request);
        return APIResponse.<PaymentResponse>builder()
                .message("Create Payment successful")
                .result(result)
                .build();
    }

    /**
     * Callback từ VNPay sau khi thanh toán
     */
    @GetMapping("/vnpay/callback")
    public ResponseEntity<?> handleVNPayCallback(
            @RequestParam Map<String, String> params,
            HttpServletRequest request) {
        log.info("VNPay callback received with params: {}", params.keySet());
        try {
            paymentService.handleCallback("vnpay", params);
            // Redirect về frontend với status success
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/payment/success"))
                    .build();
        } catch (Exception e) {
            log.error("Payment callback error: {}", e.getMessage(), e);
            // Redirect về frontend với status failed
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/payment/failed?error=" + e.getMessage()))
                    .build();
        }
    }

    /**
     * Lấy trạng thái payment
     */
    @GetMapping("/{paymentId}/status")
    public APIResponse<?> getPaymentStatus(@PathVariable String paymentId) {
        log.info("Getting payment status: {}", paymentId);
        return APIResponse.builder()
                .result(paymentService.getPaymentStatus(paymentId))
                .message("Get payment status successfully")
                .build();
    }

    /**
     * Lấy lịch sử thanh toán của user
     */
    @GetMapping("/history")
    public APIResponse<PageResponse<?>> getPaymentHistory(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Getting payment history");
        return APIResponse.<PageResponse<?>>builder()
                .result(paymentService.getPaymentHistory(pageable))
                .message("Get payment history successfully")
                .build();
    }

//    @PostMapping("/cancel")
//    public APIResponse<String> cancelEnrollment(@RequestParam String courseId) {
//        log.info("Controller: cancelEnrollment");
//        paymentService.cancelEnrollment(courseId);
//        return APIResponse.<String>builder()
//                .result("Hủy đăng ký thành công")
//                .build();
//    }

}
