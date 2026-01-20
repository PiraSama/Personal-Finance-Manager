package com.thotran.sochitieu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO cho request tạo/cập nhật ngân sách.
 */
@Data
public class BudgetRequest {
    
    @NotNull(message = "Số tiền ngân sách không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền ngân sách phải lớn hơn 0")
    private BigDecimal amount;
    
    @NotNull(message = "Tháng không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1-12")
    @Max(value = 12, message = "Tháng phải từ 1-12")
    private Integer month;
    
    @NotNull(message = "Năm không được để trống")
    @Min(value = 2020, message = "Năm phải từ 2020 trở đi")
    private Integer year;
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}
