package com.thotran.sochitieu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO trả về khi có lỗi xảy ra.
 * Format JSON chuẩn cho tất cả các error response.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Không trả về field null
public class ErrorResponse {
    
    private int code;           // HTTP status code
    private String error;       // Tên mã lỗi (VD: USER_NOT_FOUND)
    private String message;     // Thông báo lỗi chi tiết
    private LocalDateTime timestamp;
    private String path;        // API endpoint gây lỗi (optional)
}
