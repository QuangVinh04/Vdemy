package V1Learn.spring.mapper;




import V1Learn.spring.dto.request.CartCreationRequest;
import V1Learn.spring.dto.response.CartItemResponse;
import V1Learn.spring.dto.response.CartResponse;
import V1Learn.spring.entity.Cart;
import V1Learn.spring.entity.CartItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CartMapper {

    Cart toCart(CartCreationRequest request);

    default CartResponse toCartResponse(Cart cart){
        return CartResponse.builder()
                .userId(cart.getUser().getId())
                .items(cart.getItems().stream()
                        .map(this::toCartItemResponse)
                        .toList())
                .build();
    }
    
    default CartItemResponse toCartItemResponse(CartItem item) {
        return CartItemResponse.builder()

                .build();
    }


}
