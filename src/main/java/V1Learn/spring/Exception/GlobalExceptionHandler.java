package V1Learn.spring.Exception;



import V1Learn.spring.DTO.Response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // Xử lý lỗi hệ thống chung (lỗi không được xử lý trước đó)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode())
                .body(errorResponse);
    }


    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(
                        APIResponse.builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build()
                );
    }

    // xử lý Exception nhập dữ liệu không hợp lệ @Valid
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e , HttpServletRequest request) {

        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errors = fieldErrors.stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage())) // Tạo thông điệp chi tiết cho từng lỗi
                .toList();

        return ResponseEntity
                .status(ErrorCode.VALIDATION_REQUIRED_FIELD.getStatusCode())
                .body(ErrorResponse.builder()
                        .status(ErrorCode.VALIDATION_REQUIRED_FIELD.getCode())
                        .message(errors.size() > 1 ? String.join("; ", errors) : errors.get(0))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // xử lý Exception không có quyền truy cập
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handlingAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.AUTH_UNAUTHORIZED;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(errorCode.getCode());
        errorResponse.setMessage(errorCode.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getRequestURI());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(errorResponse);
    }


}
