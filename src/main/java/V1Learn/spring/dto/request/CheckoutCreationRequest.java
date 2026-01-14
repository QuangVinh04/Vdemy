package V1Learn.spring.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutCreationRequest {
    String paymentMethodId;
    String promotionCode;
    List<CheckoutItemRequest> items;
}
