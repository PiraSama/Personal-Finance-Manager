package com.thotran.sochitieu.dto.response;

import com.thotran.sochitieu.entity.RecurringFrequency;
import com.thotran.sochitieu.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO trả về thông tin recurring transaction.
 */
@Data
@Builder
public class RecurringTransactionResponse {
    
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private RecurringFrequency frequency;
    private String frequencyDisplay;    // Human readable: "Hàng tháng"
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextDueDate;
    private Boolean isActive;
    
    // Thông tin category
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
}
