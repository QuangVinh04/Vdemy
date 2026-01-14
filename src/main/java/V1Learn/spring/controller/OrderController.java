package V1Learn.spring.controller;

import V1Learn.spring.dto.request.CheckoutCreationRequest;
import V1Learn.spring.dto.response.APIResponse;
import V1Learn.spring.dto.response.OrderResponse;
import V1Learn.spring.dto.response.PageResponse;
import V1Learn.spring.service.CheckoutService;
import V1Learn.spring.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/order")
@Slf4j
public class OrderController {
    OrderService orderService;


    /**
     * Lấy danh sách orders của user hiện tại
     */
    @GetMapping
    public APIResponse<PageResponse<?>> getMyOrders(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Getting user orders");
        return APIResponse.<PageResponse<?>>builder()
                .result(orderService.getMyOrders(pageable))
                .message("Get orders successfully")
                .build();
    }

    /**
     * Lấy thông tin chi tiết order theo ID
     */
    @GetMapping("/{orderId}")
    public APIResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        log.info("Getting order: {}", orderId);
        return APIResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .message("Get order successfully")
                .build();
    }

    /**
     * Hủy order (chỉ khi order chưa thanh toán)
     */
    @PutMapping("/{orderId}/cancel")
    public APIResponse<Void> cancelOrder(@PathVariable String orderId) {
        log.info("Cancelling order: {}", orderId);
        orderService.cancelOrder(orderId);
        return APIResponse.<Void>builder()
                .message("Order cancelled successfully")
                .build();
    }


}