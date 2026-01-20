package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO trả về tổng quan tài chính cho Dashboard.
 */
@Data
@Builder
public class DashboardSummaryResponse {
    
    private BigDecimal totalIncome;     // Tổng thu nhập trong kỳ
    private BigDecimal totalExpense;    // Tổng chi tiêu trong kỳ
    private BigDecimal balance;         // Số dư (thu - chi)
    private Double savingsRate;         // Tỷ lệ tiết kiệm (%)
    private Long transactionCount;      // Số giao dịch trong kỳ
    
    // Top categories chi tiêu nhiều nhất
    private List<CategoryExpenseResponse> topExpenseCategories;
    
    /**
     * Inner class cho top expense categories
     */
    @Data
    @Builder
    public static class CategoryExpenseResponse {
        private Long categoryId;
        private String categoryName;
        private String categoryIcon;
        private String categoryColor;
        private BigDecimal amount;
        private Double percent;         // % so với tổng chi tiêu
    }
}
