package V1Learn.spring.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayConfig {

    @Getter
    @Value("${payment.vnPay.url}")
    String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    String vnp_TmnCode;

    @Getter
    @Value("${payment.vnPay.secretKey}")
    String secretKey;

    @Value("${payment.vnPay.version}")
    String vnp_Version;

    @Value("${payment.vnPay.command}")
    String vnp_Command;

    @Value("${payment.vnPay.orderType}")
    String orderType;



    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", this.vnp_Version);
        vnpParams.put("vnp_Command", this.vnp_Command);
        vnpParams.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_OrderType", this.orderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        return vnpParams;
    }


}
