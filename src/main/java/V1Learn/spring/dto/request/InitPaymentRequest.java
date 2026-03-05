package V1Learn.spring.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitPaymentRequest {
    @NotBlank
    String checkoutId;

    @NotNull(message = "Payment method is required")
    String paymentMethod;

}
