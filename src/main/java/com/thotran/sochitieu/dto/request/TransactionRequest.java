package com.thotran.sochitieu.dto.request;

import com.thotran.sochitieu.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO cho request tạo/cập nhật giao dịch.
 */
@Data
public class TransactionRequest {
    
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;
    
    @NotNull(message = "Loại giao dịch không được để trống (INCOME hoặc EXPENSE)")
    private TransactionType type;
    
    private String description;     // Mô tả giao dịch (optional)
    
    private LocalDate transactionDate;  // Ngày giao dịch (nếu null sẽ lấy ngày hiện tại)
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}
