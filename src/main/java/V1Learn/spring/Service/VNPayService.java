package V1Learn.spring.Service;

import V1Learn.spring.Entity.*;
import V1Learn.spring.config.VNPayConfig;
import V1Learn.spring.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService  {

    VNPayConfig vnPayConfig;


    public String createVNPayPayment(Enrollment enrollment, HttpServletRequest request) {
        Map<String, String> params = vnPayConfig.getVNPayConfig();
        params.put("vnp_Amount", enrollment.getCourse().getPrice().multiply(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(25000))
                .toBigInteger().toString());
        params.put("vnp_BankCode", "NCB");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        params.put("vnp_ExpireDate", formatter.format(LocalDateTime.now().plusHours(1)));
        params.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8) + "_" + enrollment.getId());
        params.put("vnp_OrderInfo", "Test");
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
        return vnPayConfig.getVnp_PayUrl() + "?" + query;
    }

    // kiểm tra tính hợp lệ của chữ ký
    public boolean verifyPayment(Map<String, String> params) {
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
