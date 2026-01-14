package V1Learn.spring.service.handler;

import V1Learn.spring.dto.request.CapturePaymentRequest;

import V1Learn.spring.entity.Payment;
import V1Learn.spring.model.payment.CapturedPayment;
import V1Learn.spring.model.payment.InitiatedPayment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentHandler {
    String getProviderName();

    InitiatedPayment initPayment(Payment payment,  HttpServletRequest request);

    CapturedPayment handleCallback(Map<String, String> params);

}
