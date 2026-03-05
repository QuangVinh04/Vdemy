package V1Learn.spring.service.handler;

import V1Learn.spring.config.VNPayConfig;
import V1Learn.spring.entity.Payment;
import V1Learn.spring.enums.PaymentMethod;
import V1Learn.spring.enums.PaymentStatus;
import V1Learn.spring.model.payment.CapturedPayment;
import V1Learn.spring.model.payment.InitiatedPayment;
import V1Learn.spring.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayHandler implements PaymentHandler {

    VNPayConfig vnPayConfig;


    @Override
    public String getProviderName() {
        return PaymentMethod.VN_PAY.name();
    }

    @Override
    public InitiatedPayment initPayment(Payment payment, HttpServletRequest request) {

        Map<String, String> params = vnPayConfig.getVNPayConfig();
        params.put("vnp_Amount", payment.getAmount().multiply(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(25000))
                .toBigInteger().toString());
        params.put("vnp_BankCode", "NCB");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        params.put("vnp_ExpireDate", formatter.format(LocalDateTime.now().plusHours(1)));
        params.put("vnp_TxnRef", payment.getTransactionId());
        params.put("vnp_OrderInfo", payment.getDescription());
        params.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        List<String> sortedFieldNames = new ArrayList<>(params.keySet());
        Collections.sort(sortedFieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for(Iterator<String> iterator = sortedFieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            String value = params.get(fieldName);

            if(value != null && !value.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if(iterator.hasNext()) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        String url = vnPayConfig.getVnp_PayUrl() + "?" + query;

        return InitiatedPayment.builder()
                .redirectUrl(url)
                .expiredAt(LocalDateTime.now().plusHours(1))
                .build();
    }



    @Override
    public CapturedPayment handleCallback(Map<String, String> params) {
        if (!verifySignature(params)) {
            throw new RuntimeException("Invalid VNPay signature");
        }

        String responseCode = params.get("vnp_ResponseCode");

        return CapturedPayment.builder()
                .transactionId(params.get("vnp_TxnRef"))
                .gatewayTransactionId(params.get("vnp_TransactionNo"))
                .amount(new BigDecimal(params.get("vnp_Amount"))
                        .divide(BigDecimal.valueOf(100)))
                .paymentStatus(
                        "00".equals(responseCode)
                                ? PaymentStatus.COMPLETED
                                : PaymentStatus.FAILED
                )
                .rawResponse(params.toString())
                .build();
    }


    // kiểm tra tính hợp lệ của chữ ký
    private boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        Map<String, String> filteredParams = new TreeMap<>(params);
        filteredParams.remove("vnp_SecureHash");
        filteredParams.remove("vnp_SecureHashType");

        String queryString = VNPayUtil.getPaymentURL(filteredParams, false);
        String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), queryString);
        // xác thực chữ ký trước và sau khi VNPay gửi vê
        return calculatedHash.equals(receivedHash);
    }
}
