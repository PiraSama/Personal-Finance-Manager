package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO cho so sánh 2 tháng.
 */
@Data
@Builder
public class MonthComparisonResponse {
    
    // Tháng 1
    private String month1;              // Format: "MM/yyyy"
    private BigDecimal income1;
    private BigDecimal expense1;
    private BigDecimal balance1;
    
    // Tháng 2
    private String month2;              // Format: "MM/yyyy"
    private BigDecimal income2;
    private BigDecimal expense2;
    private BigDecimal balance2;
    
    // Chênh lệch (tháng 2 - tháng 1)
    private BigDecimal incomeDiff;
    private BigDecimal expenseDiff;
    private BigDecimal balanceDiff;
    
    // % thay đổi
    private Double incomeChangePercent;
    private Double expenseChangePercent;
}
