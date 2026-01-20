package com.thotran.sochitieu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cho request cập nhật thông tin user.
 */
@Data
public class UserUpdateRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    
    // Password update sẽ là API riêng (để đảm bảo bảo mật)
}
