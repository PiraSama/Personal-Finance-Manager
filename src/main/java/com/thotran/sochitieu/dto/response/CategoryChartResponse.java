package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO cho data biểu đồ tròn chi tiêu theo category.
 */
@Data
@Builder
public class CategoryChartResponse {
    
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private BigDecimal amount;          // Số tiền
    private Double percent;             // % so với tổng
}
