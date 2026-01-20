package com.thotran.sochitieu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO wrapper chuẩn cho tất cả API response.
 * Đảm bảo format thống nhất cho toàn bộ ứng dụng.
 * 
 * @param <T> Kiểu dữ liệu của data trả về
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    @Builder.Default
    private boolean success = true;     // Trạng thái thành công
    private String message;             // Thông báo (optional)
    private T data;                     // Dữ liệu trả về
    
    // === Factory methods để tạo response nhanh ===
    
    /**
     * Tạo response thành công với data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    /**
     * Tạo response thành công với message và data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Tạo response thành công chỉ với message (không có data)
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
}
