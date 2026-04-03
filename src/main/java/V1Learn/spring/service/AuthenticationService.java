package V1Learn.spring.service;

import V1Learn.spring.dto.request.*;
import V1Learn.spring.dto.response.AuthenticationResponse;
import V1Learn.spring.dto.response.IntrospectResponse;
import V1Learn.spring.dto.response.RefreshTokenResponse;
import V1Learn.spring.entity.Role;
import V1Learn.spring.entity.User;
import V1Learn.spring.exception.AppException;
import V1Learn.spring.exception.ErrorCode;
import V1Learn.spring.repository.RoleRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.repository.httpclient.OutboundIdentityClient;
import V1Learn.spring.repository.httpclient.OutboundUserClient;
import V1Learn.spring.security.CustomUserDetails;
import V1Learn.spring.constant.PredefinedRole;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service quản lý xác thực của người dùng
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    RedisService redisService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    OutboundUserClient outboundUserClient;
    OutboundIdentityClient outboundIdentityClient;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(String token) throws JOSEException, ParseException {

        boolean isValid = true;
        Set<String> roles = new HashSet<>();
        try {
            SignedJWT signedJWT = jwtService.verifyToken(token);
            Object claim = signedJWT.getJWTClaimsSet().getClaim("scope");

            if (claim instanceof String scopeString) {
                roles = Arrays.stream(scopeString.split(" "))
                        .collect(Collectors.toSet());
            }
        } catch (AppException e) {
            isValid = false;

        }
        log.info("Introspect token successful");
        return IntrospectResponse.builder()
                .valid(isValid)
                .scope(roles)
                .build();
    }

    public AuthenticationResponse outboundAuthenticate(String code, HttpServletResponse httpResponse)
            throws ParseException {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("Token response {}", response);

        // Get user info
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("User Info {}", userInfo);

        Set<Role> roles = new HashSet<>();
        Role roleUser = roleRepository.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS));
        roles.add(roleUser);

        User user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (user == null) {
            // Tạo mới user với role mặc định
            user = User.builder()
                    .email(userInfo.getEmail())
                    .firstName(userInfo.getGivenName())
                    .lastName(userInfo.getFamilyName())
                    .roles(roles)
                    .build();
            userRepository.save(user);
        } else {
            // User đã tồn tại, nếu cần update role thì làm ở đây
            // Ví dụ: nếu chưa có role USER_ROLE thì add vào
            if (!user.getRoles().contains(roleUser)) {
                user.getRoles().add(roleUser);
                userRepository.save(user);
            }
        }

        // Generate token
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // lấy thông tin của refresh token sau khi tạo ra
        SignedJWT signedTokenRefresh = SignedJWT.parse(refreshToken);
        Date expiryTime = signedTokenRefresh.getJWTClaimsSet().getExpirationTime();

        // Lưu refresh token vào Redis có key là refresh + userId;
        redisService.setUntil("refresh:" + user.getId(), refreshToken, expiryTime.getTime());

        addRefreshToCookie(refreshToken, httpResponse);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response)
            throws ParseException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        userDetails.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        log.info("User logged in successfully. userId={}, email={}, roles={}",
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // 4. Xử lý Redis (Nên bọc try-catch nếu không muốn app sập khi Redis lỗi)
        try {
            SignedJWT signedTokenRefresh = SignedJWT.parse(refreshToken);
            Date expiryTime = signedTokenRefresh.getJWTClaimsSet().getExpirationTime();
            redisService.setUntil("refresh:" + user.getId(), refreshToken, expiryTime.getTime());
            log.info("Refresh token stored in Redis for userId={}, expiry={}", user.getId(), expiryTime);
        } catch (Exception e) {
            log.error("Lỗi lưu Redis: {}", e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        addRefreshToCookie(refreshToken, response);
        log.info("Login with email and password successful");
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public void logout(LogoutRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        try {
            var signedToken = jwtService.verifyToken(request.getAccessToken());
            String jti = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();
            String userId = signedToken.getJWTClaimsSet().getSubject();
            redisService.setUntil("blacklist:" + jti, "true",  expiryTime.getTime());
            // Xóa RefreshToken trong Redis
            redisService.deleteValue("refresh:" + userId);

            deleteRefreshTokenCookie(response);
        } catch (AppException exception) {
            log.info("Token already expired or invalid, just clearing cookies");
            deleteRefreshTokenCookie(response);
        }
    }

    public RefreshTokenResponse refreshToken(String refreshToken, String accessToken)
            throws ParseException, JOSEException {

        var signedJWT = jwtService.verifyToken(refreshToken);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String refreshKey = "refresh:" + userId;

        // 1. Kiểm tra Refresh Token trong Redis
        Object storedValue = redisService.getValue(refreshKey);
        if (storedValue == null || !storedValue.toString().equals(refreshToken)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        // 2. Blacklist Access Token cũ (nếu nó chưa hết hạn)
        if (accessToken != null) {
            try {
                SignedJWT oldJwt = SignedJWT.parse(accessToken);
                String jti = oldJwt.getJWTClaimsSet().getJWTID();
                long exp = oldJwt.getJWTClaimsSet().getExpirationTime().getTime();
                redisService.setUntil("blacklist:" + jti, "true", exp);

            } catch (Exception e) {
                log.warn("Could not blacklist old access token: {}", e.getMessage());
            }
        }
        // 3. Tạo mới Access Token
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        var newAccessToken = jwtService.generateAccessToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .userId(user.getId())
                .build();
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void addRefreshToCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true nếu chỉ cho gửi qua HTTPS
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

}
