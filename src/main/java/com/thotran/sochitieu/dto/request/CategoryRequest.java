package com.thotran.sochitieu.dto.request;

import com.thotran.sochitieu.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO cho request tạo/cập nhật danh mục.
 */
@Data
public class CategoryRequest {
    
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    
    @NotNull(message = "Loại danh mục không được để trống (INCOME hoặc EXPENSE)")
    private TransactionType type;
    
    private String icon;    // Optional: emoji hoặc tên icon
    private String color;   // Optional: mã màu hex
}
