package com.thotran.sochitieu.dto.request;

import com.thotran.sochitieu.entity.RecurringFrequency;
import com.thotran.sochitieu.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO cho request tạo/cập nhật recurring transaction.
 */
@Data
public class RecurringTransactionRequest {
    
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;
    
    @NotNull(message = "Loại giao dịch không được để trống")
    private TransactionType type;
    
    private String description;
    
    @NotNull(message = "Tần suất không được để trống")
    private RecurringFrequency frequency;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;
    
    private LocalDate endDate;      // Optional: null = vô thời hạn
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}
