package V1Learn.spring.controller;


import V1Learn.spring.dto.request.CartCreationRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.CartResponse;
import V1Learn.spring.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {

    CartService cartService;

    @GetMapping
    public APIResponse<CartResponse> getCart(@AuthenticationPrincipal(expression = "subject") String userId) {
        return APIResponse.<CartResponse>builder()
                .result(cartService.getCart(userId))
                .message("get cart successfully")
                .build();
    }

    @PostMapping("/items")
    public APIResponse<CartResponse> addCourseToCart(@RequestBody CartCreationRequest request) {
        return APIResponse.<CartResponse>builder()
                .result(cartService.addToCart(request))
                .message("add course to cart successfully")
                .build();
    }

    @DeleteMapping("/{courseId}")
    public APIResponse<Void> removeCourseFromCart(@AuthenticationPrincipal(expression = "subject") String userId,
                                                  @PathVariable String courseId) {
        cartService.removeCourseFromCart(userId, courseId);
        return APIResponse.<Void>builder()
                .message("delete course from cart successfully")
                .build();
    }


    /**
     * Xóa tất cả items trong cart
     */
    @DeleteMapping
    public APIResponse<Void> clearCart(
            @AuthenticationPrincipal(expression = "subject") String userId) {
        log.info("Clearing cart for user: {}", userId);
        cartService.clearCart(userId);
        return APIResponse.<Void>builder()
                .message("Clear cart successfully")
                .build();
    }

}
