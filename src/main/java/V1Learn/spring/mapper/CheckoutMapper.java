package V1Learn.spring.mapper;




import V1Learn.spring.dto.request.CheckoutCreationRequest;
import V1Learn.spring.dto.request.CheckoutItemRequest;
import V1Learn.spring.dto.response.CheckoutItemResponse;
import V1Learn.spring.dto.response.CheckoutResponse;
import V1Learn.spring.entity.Checkout;
import V1Learn.spring.entity.CheckoutItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CheckoutMapper {


    @Mapping(target = "items", ignore = true)
    Checkout toCheckout(CheckoutCreationRequest request);


     default CheckoutResponse toCheckoutResponse(Checkout checkout){
        return CheckoutResponse.builder()
                .id(checkout.getId())
                .paymentMethodId(checkout.getPaymentMethodId())
                .checkoutState(checkout.getCheckoutState())
                .totalAmount(checkout.getTotalAmount())
                .totalDiscountAmount(checkout.getTotalDiscountAmount())
                .promotionCode(checkout.getPromotionCode())
                .items(checkout.getItems().stream()
                        .map(this::toCheckoutItemResponse)
                        .collect(Collectors.toSet()))
                .build();

    }

    CheckoutItem toCheckoutItem(CheckoutItemRequest item);

    CheckoutItemResponse toCheckoutItemResponse(CheckoutItem item);


}
