package V1Learn.spring.Service;

import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.AuthenticationResponse;
import V1Learn.spring.DTO.Response.IntrospectResponse;
import V1Learn.spring.DTO.Response.RefreshTokenResponse;
import V1Learn.spring.Entity.Role;
import V1Learn.spring.Entity.User;
import V1Learn.spring.Exception.AppException;
import V1Learn.spring.Exception.ErrorCode;
import V1Learn.spring.Repostiory.RoleRepository;
import V1Learn.spring.Repostiory.UserRepository;
import V1Learn.spring.Repostiory.httpclient.OutboundIdentityClient;
import V1Learn.spring.Repostiory.httpclient.OutboundUserClient;
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
import org.springframework.beans.factory.annotation.Autowired;
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
    private final RoleService roleService;
    private final RoleRepository roleRepository;

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


     //kiểm tra tính hợp lệ của token
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
        }
        catch (AppException e) {
            isValid = false;

        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .scope(roles)
                .build();
    }

    public AuthenticationResponse outboundAuthenticate(String code, HttpServletResponse httpResponse) throws ParseException {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

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
        redisService.setWithTTL("refresh:" + user.getId(), refreshToken, expiryTime.getTime(), TimeUnit.MILLISECONDS);

        addRefreshToCookie(refreshToken, httpResponse);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) throws ParseException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        user.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        log.info("User logged in successfully. userId={}, email={}, roles={}",
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // lấy thông tin của refresh token sau khi tạo ra
        SignedJWT signedTokenRefresh = SignedJWT.parse(refreshToken);
        Date expiryTime = signedTokenRefresh.getJWTClaimsSet().getExpirationTime();

        // Lưu refresh token vào Redis có key là refresh + userId;
        redisService.setWithTTL("refresh:" + user.getId(), refreshToken, expiryTime.getTime(), TimeUnit.MILLISECONDS);

        log.debug("Refresh token stored in Redis for userId={}, expiry={}", user.getId(), expiryTime);


        addRefreshToCookie(refreshToken, response);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public void logout(LogoutRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        try {
            var signedToken = jwtService.verifyToken(request.getAccessToken());
            String jit = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

            String refreshKey = "refresh:" + signedToken.getJWTClaimsSet().getSubject();

            // Đánh dấu Access Token là blacklist + jit
            redisService.setWithTTL("blacklist:" + jit, request.getAccessToken(), expiryTime.getTime(), TimeUnit.MILLISECONDS);
            // Xóa RefreshToken trong Redis
            redisService.deleteValue(refreshKey);
            // xóa khỏi cookie
            deleteRefreshTokenCookie(response);
        } catch (AppException exception){
            log.info("Token already expired");
        }
    }

    public RefreshTokenResponse refreshToken(String refreshToken, String accessToken) throws ParseException, JOSEException {
        // kiểm tra refreshToken lưu trong redis
        var signedJWT =  jwtService.verifyToken(refreshToken);
        String refreshKey = "refresh:" + signedJWT.getJWTClaimsSet().getSubject();
        String storedRefreshToken = redisService.getValue(refreshKey).toString();
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        // Thêm accessToken vào blacklist
        if(accessToken != null) {
            SignedJWT jwt = SignedJWT.parse(accessToken);
            var jwtTid = jwt.getJWTClaimsSet().getJWTID();
            long accessTokenExp = jwt.getJWTClaimsSet().getExpirationTime().getTime();
            redisService.setWithTTL("blacklist:" + jwtTid, accessToken, accessTokenExp, TimeUnit.MILLISECONDS);
        }

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED)
        );
        // tạo access token mới
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

