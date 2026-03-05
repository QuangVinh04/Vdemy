package V1Learn.spring.dto.event;



import lombok.*;

@Data
@AllArgsConstructor
public class PaymentResultEvent {
    private String transactionId;
    private String status; // SUCCESS | FAILED
}

