package V1Learn.spring.dto.event;




public record PaymentResultEvent (
    String transactionId,
    String status
){}
