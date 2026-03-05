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
import org.springframework.security.access.AccessDeniedException;
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
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new AccessDeniedException("Missing Token");
                    }

                    String token = authHeader.substring(7);

                    try {
                        // Tận dụng hàm verifyToken của bạn ở đây
                        SignedJWT signedJWT = jwtService.verifyToken(token);

                        String userId = signedJWT.getJWTClaimsSet().getSubject();

                        // Trích xuất quyền (Authorities) từ signedJWT
                        var authorities = extractAuthorities(signedJWT);

                        Authentication auth = new PreAuthenticatedAuthenticationToken(
                                userId, null, authorities
                        );

                        accessor.setUser(auth);
                        log.info("WebSocket connection authenticated for user: {}", userId);

                    } catch (Exception e) {
                        log.error("WebSocket auth failed: {}", e.getMessage());
                        // Chuyển đổi mọi lỗi thành AccessDeniedException để ngắt kết nối
                        throw new AccessDeniedException("Unauthorized");
                    }
                }
                return message;
            }
        });
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
