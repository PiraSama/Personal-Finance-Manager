package com.thotran.sochitieu.dto.response;

import com.thotran.sochitieu.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

/**
 * DTO trả về thông tin danh mục.
 */
@Data
@Builder
public class CategoryResponse {
    
    private Long id;
    private String name;
    private TransactionType type;   // INCOME hoặc EXPENSE
    private String icon;
    private String color;
}
