package V1Learn.spring.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SecurityUtils {

    // lấy giá trị id đăng nhập hiện tại
    public static Optional<String> getCurrentUserId(){
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(contextHolder.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication){
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt.getSubject();
    }


    // Kiểm tra role có trùng với role của tài khoản đăng nhập không
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }


}