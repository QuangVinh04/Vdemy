package V1Learn.spring.mapper;



import V1Learn.spring.dto.response.OrderItemResponse;
import V1Learn.spring.dto.response.OrderResponse;
import V1Learn.spring.entity.Order;
import V1Learn.spring.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface OrderMapper {


     default OrderResponse toOrderResponse(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .totalDiscountAmount(order.getDiscountAmount())
                .items(order.getOrderItems().stream()
                        .map(this::toOrderItemResponse)
                        .collect(Collectors.toSet()))
                .build();

    }

    OrderItemResponse toOrderItemResponse(OrderItem item);


}
