package V1Learn.spring.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

// Chịu trách nhiệm verify Token kiểm tra xem token còn hạn hay đã logout chưa
// và giải mã token
// JwtDecoder: chịu trách nhiệm giải mã JWT
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;


//    NimbusJwtDecoder là một công cụ trong Spring Security hỗ trợ giải mã và xác thực chữ ký JWT.
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        // Khởi tạo nimbusJwtDecoder nếu chưa tồn tại
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        // giải mã token
        return nimbusJwtDecoder.decode(token);
    }
}
