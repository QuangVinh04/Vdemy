package V1Learn.spring.controller;


import V1Learn.spring.DTO.Request.*;
import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.AuthenticationResponse;
import V1Learn.spring.DTO.Response.IntrospectResponse;
import V1Learn.spring.DTO.Response.RefreshTokenResponse;
import V1Learn.spring.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;


    @PostMapping("/outbound/authentication")
    public APIResponse<AuthenticationResponse> outboundAuthenticateGoogle(@RequestParam("code") String code,
                                                                          HttpServletResponse response) throws ParseException {
        log.info("Login with username and password");
        var result = authenticationService.outboundAuthenticate(code, response);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }



    @PostMapping("/login")
    APIResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request,
                                                      HttpServletResponse response) throws ParseException {
        log.info("Login with Google");
        var result = authenticationService.authenticate(request, response);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> introspected(@RequestHeader(name = "Authorization") String authHeader) throws ParseException, JOSEException {
        log.info("Introspect token");
        String token = extractToken(authHeader);
        var result = authenticationService.introspect(token);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestBody LogoutRequest request,
                             HttpServletResponse response) throws ParseException, JOSEException {
        log.info("Logout user");
        authenticationService.logout(request, response);
        return APIResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    APIResponse<RefreshTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken,
                                              @RequestHeader(name = "Authorization", required = false) String authHeader) throws ParseException, JOSEException {
        log.info("Refresh token");
        String accessToken = extractToken(authHeader);
        RefreshTokenResponse result = authenticationService.refreshToken(refreshToken, accessToken);
        return APIResponse.<RefreshTokenResponse>builder()
                .result(result)
                .build();
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    // Void là một lớp đại diện cho kiểu void trong generic.
}
