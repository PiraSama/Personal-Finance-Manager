package com.thotran.sochitieu.exception;

import com.thotran.sochitieu.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global Exception Handler.
 * Bắt tất cả exception và trả về JSON error response chuẩn.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Xử lý AppException - exception tự định nghĩa của ứng dụng
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex, 
            HttpServletRequest request) {
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ex.getErrorCode().getHttpStatusCode())
                .error(ex.getErrorCode().name())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatusCode())
                .body(response);
    }
    
    /**
     * Xử lý lỗi validation (từ @Valid annotation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // Gom tất cả lỗi validation thành 1 message
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        ErrorResponse response = ErrorResponse.builder()
                .code(400)
                .error(ErrorCode.INVALID_INPUT.name())
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Xử lý tất cả exception khác (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        ErrorResponse response = ErrorResponse.builder()
                .code(500)
                .error(ErrorCode.UNCATEGORIZED_EXCEPTION.name())
                .message("Đã xảy ra lỗi: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.internalServerError().body(response);
    }
}
