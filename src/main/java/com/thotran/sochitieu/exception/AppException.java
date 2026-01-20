package com.thotran.sochitieu.exception;

import lombok.Getter;

/**
 * Custom Exception của ứng dụng.
 * Sử dụng ErrorCode để xác định loại lỗi.
 */
@Getter
public class AppException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor với message tùy chỉnh
     * (vẫn giữ errorCode để lấy HTTP status)
     */
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
