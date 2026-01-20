package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO trả về thông tin ngân sách.
 */
@Data
@Builder
public class BudgetResponse {
    
    private Long id;
    private BigDecimal amount;          // Ngân sách đặt ra
    private BigDecimal spent;           // Đã chi tiêu (tính từ transactions)
    private BigDecimal remaining;       // Còn lại (amount - spent)
    private Double percentUsed;         // Phần trăm đã dùng
    private Integer month;
    private Integer year;
    
    // Thông tin danh mục
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
}
