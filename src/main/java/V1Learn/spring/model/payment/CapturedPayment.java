package V1Learn.spring.model.payment;


import V1Learn.spring.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CapturedPayment {
    String transactionId;       // map với Payment.transactionId
    String gatewayTransactionId; // mã giao dịch từ VNPay / PayPal
    BigDecimal amount;           // số tiền gateway báo đã thanh toán
    PaymentStatus paymentStatus; // COMPLETED / FAILED
    String rawResponse;           // raw data để debug / audit
}
