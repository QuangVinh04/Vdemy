package V1Learn.spring.config;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.Exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper; // công cụ của Jackson giúp chuyển đổi đối tượng Java thành JSON.
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException; // Ngoại lệ này sẽ được ném ra khi xác thực thất bại.
import org.springframework.security.web.AuthenticationEntryPoint; // Interface giúp tùy chỉnh phản hồi khi có lỗi xác thực.

import java.io.IOException;
//Đây là một lớp xử lý khi xảy ra lỗi xác thực (AuthenticationException)
// trong ứng dụng Spring Security có sử dụng JSON Web Token (JWT)
// =>
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.AUTH_UNAUTHORIZED;

        //Đặt mã HTTP status của response thành 401
        response.setStatus(errorCode.getStatusCode().value());
        //Xác định kiểu phản hồi là JSON.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //Tạo đối tượng phản hồi API
        APIResponse<?> apiResponse = APIResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        //Chuyển đối tượng phản hồi thành JSON và gửi về client
        ObjectMapper objectMapper = new ObjectMapper();
        // Ghi chuỗi JSON vào phản hồi HTTP.
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer(); // Đảm bảo dữ liệu được gửi ngay lập tức.
    }
}
