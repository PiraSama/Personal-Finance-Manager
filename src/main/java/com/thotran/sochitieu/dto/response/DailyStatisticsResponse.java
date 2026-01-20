package com.thotran.sochitieu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO cho thống kê chi tiêu theo ngày (calendar view).
 */
@Data
@Builder
public class DailyStatisticsResponse {
    
    private Integer day;                // Ngày trong tháng (1-31)
    private String date;                // Format: "yyyy-MM-dd"
    private BigDecimal income;          // Tổng thu trong ngày
    private BigDecimal expense;         // Tổng chi trong ngày
    private Long transactionCount;      // Số giao dịch trong ngày
}
