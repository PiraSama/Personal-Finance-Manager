package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO cho data biểu đồ xu hướng thu/chi theo tháng.
 */
@Data
@Builder
public class MonthlyTrendResponse {
    
    private String month;               // Format: "MM/yyyy" (VD: "01/2026")
    private Integer monthValue;         // Tháng (1-12)
    private Integer year;               // Năm
    private BigDecimal income;          // Tổng thu
    private BigDecimal expense;         // Tổng chi
    private BigDecimal balance;         // Thu - Chi
}
