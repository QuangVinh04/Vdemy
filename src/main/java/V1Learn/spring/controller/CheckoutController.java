package V1Learn.spring.controller;

import V1Learn.spring.dto.request.ChapterRequest;
import V1Learn.spring.dto.request.CheckoutCreationRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.ChapterResponse;
import V1Learn.spring.dto.response.CheckoutResponse;
import V1Learn.spring.service.ChapterService;
import V1Learn.spring.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/checkout")
@Slf4j
public class CheckoutController {
    CheckoutService checkoutService;

    /**
     * Tạo checkout mới từ cart hoặc danh sách courses
     */
    @PostMapping
    public APIResponse<CheckoutResponse> createCheckout(
            @RequestBody @Valid CheckoutCreationRequest request) {
        log.info("Creating checkout");
        CheckoutResponse result = checkoutService.createCheckout(request);
        return APIResponse.<CheckoutResponse>builder()
                .result(result)
                .message("Checkout created successfully")
                .build();
    }

    /**
     * Lấy thông tin checkout theo ID
     */
    @GetMapping("/{checkoutId}")
    public APIResponse<CheckoutResponse> getCheckout(@PathVariable String checkoutId) {
        log.info("Getting checkout: {}", checkoutId);
        CheckoutResponse result = checkoutService.getCheckoutById(checkoutId);
        return APIResponse.<CheckoutResponse>builder()
                .result(result)
                .message("Get checkout successfully")
                .build();
    }

    // /**
    // * Cập nhật phương thức thanh toán cho checkout
    // */
    // @PutMapping("/{checkoutId}/payment-method")
    // public APIResponse<Void> updatePaymentMethod(
    // @PathVariable String checkoutId,
    // @RequestBody Map<String, String> request) {
    // log.info("Updating payment method for checkout: {}", checkoutId);
    // String paymentMethod = request.get("paymentMethod");
    // checkoutService.updateCheckoutPaymentMethod(checkoutId, paymentMethod);
    // return APIResponse.<Void>builder()
    // .message("Payment method updated successfully")
    // .build();
    // }

}