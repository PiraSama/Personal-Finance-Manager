package com.thotran.sochitieu.dto.response;

import com.thotran.sochitieu.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO trả về thông tin giao dịch.
 */
@Data
@Builder
public class TransactionResponse {
    
    private Long id;
    private BigDecimal amount;
    private TransactionType type;       // INCOME hoặc EXPENSE
    private String description;
    private LocalDate transactionDate;
    
    // Thông tin danh mục (để hiển thị trên UI)
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
}
