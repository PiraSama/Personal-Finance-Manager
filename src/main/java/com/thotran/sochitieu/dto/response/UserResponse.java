package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin user (không bao gồm password).
 */
@Data
@Builder
public class UserResponse {
    
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;
}
