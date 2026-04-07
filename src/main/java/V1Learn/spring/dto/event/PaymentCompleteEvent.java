package V1Learn.spring.dto.event;


import V1Learn.spring.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCompleteEvent {
    String userId;
    String transactionId;
    String orderCode;
    PaymentStatus status;
}

