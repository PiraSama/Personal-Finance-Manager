package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO trả về khi đăng nhập thành công.
 */
@Data
@Builder
public class AuthResponse {
    
    private String token;           // JWT token
    private String tokenType;       // Loại token (Bearer)
    private Long expiresIn;         // Thời gian hết hạn (seconds)
    private UserResponse user;      // Thông tin user
}
