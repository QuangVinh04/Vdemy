package V1Learn.spring.config;



import V1Learn.spring.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

// Chịu trách nhiệm verify Token kiểm tra xem token còn hạn hay đã logout chưa và giải mã token
// JwtDecoder: chịu trách nhiệm giải mã JWT
@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    private final RedisService redisService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        // 1. Khởi tạo decoder nếu chưa có
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        // 2. Giải mã token trước để lấy thông tin bên trong
        Jwt jwt = nimbusJwtDecoder.decode(token);

        // 3. Kiểm tra Blacklist trong Redis bằng JTI
        // JTI là ID duy nhất của mỗi token chúng ta đã lưu vào blacklist lúc logout/refresh
        String jti = jwt.getId();
        if (redisService.existsValue("blacklist:" + jti)) {
            throw new JwtException("Token has been revoked (blacklisted)");
        }

        return jwt;
    }
}
