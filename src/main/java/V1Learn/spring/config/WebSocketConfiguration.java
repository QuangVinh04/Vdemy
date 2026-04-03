package V1Learn.spring.config;

import V1Learn.spring.service.JwtService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final CustomJwtDecoder jwtDecoder;
    private final JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Thử lấy token từ nhiều nguồn
                    String token = extractToken(accessor);

                    if (token == null || token.isBlank()) {
                        // Cho phép kết nối anonymous (user chưa đăng nhập)
                        log.info("WebSocket connection without token - anonymous user");
                        return message;
                    }

                    try {
                        SignedJWT signedJWT = jwtService.verifyToken(token);

                        String userId = signedJWT.getJWTClaimsSet().getSubject();

                        var authorities = extractAuthorities(signedJWT);

                        Authentication auth = new PreAuthenticatedAuthenticationToken(
                                userId, null, authorities
                        );

                        accessor.setUser(auth);
                        log.info("WebSocket connection authenticated for user: {}", userId);

                    } catch (Exception e) {
                        log.error("WebSocket auth failed: {}", e.getMessage());
                        // Token không hợp lệ → vẫn cho kết nối anonymous thay vì ngắt
                        log.warn("Allowing anonymous WebSocket connection due to invalid token");
                    }
                }
                return message;
            }
        });
    }

    /**
     * Trích xuất JWT token từ STOMP headers
     * Thử: Authorization header → login header
     */
    private String extractToken(StompHeaderAccessor accessor) {
        // 1. Thử lấy từ header "Authorization"
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Token found in Authorization header");
            return authHeader.substring(7);
        }

        // 2. Thử lấy từ header "login" (STOMP default)
        String loginHeader = accessor.getFirstNativeHeader("login");
        if (loginHeader != null && !loginHeader.isBlank()) {
            log.debug("Token found in login header");
            return loginHeader;
        }

        // 3. Thử lấy từ header "token" (custom)
        String tokenHeader = accessor.getFirstNativeHeader("token");
        if (tokenHeader != null && !tokenHeader.isBlank()) {
            log.debug("Token found in token header");
            return tokenHeader;
        }

        return null;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(SignedJWT signedJWT) throws ParseException {
        Object scopeClaim = signedJWT.getJWTClaimsSet().getClaim("scope");

        if (scopeClaim == null) {
            return List.of();
        }

        // Trường hợp 1: Scope là chuỗi các quyền cách nhau bởi dấu cách (Dạng: "ROLE_ADMIN SCOPE_READ")
        if (scopeClaim instanceof String scopeStr && !scopeStr.isEmpty()) {
            return Arrays.stream(scopeStr.split(" "))
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        // Trường hợp 2: Scope là một danh sách (Dạng: ["ROLE_ADMIN", "SCOPE_READ"])
        if (scopeClaim instanceof List<?> scopeList) {
            return scopeList.stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        return List.of();
    }
}
