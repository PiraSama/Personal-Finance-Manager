package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.TransactionRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.TransactionResponse;
import com.thotran.sochitieu.entity.TransactionType;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller cho Transaction APIs.
 * Base path: /api/transactions
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * POST /api/transactions
     * Tạo giao dịch mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {
        
        TransactionResponse transaction = transactionService.create(getCurrentUserId(), request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo giao dịch thành công!", transaction));
    }
    
    /**
     * GET /api/transactions
     * Lấy danh sách giao dịch với các filter tùy chọn
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @param categoryId Lọc theo danh mục
     * @param type Lọc theo loại: INCOME hoặc EXPENSE
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) TransactionType type) {
        
        Long userId = getCurrentUserId();
        List<TransactionResponse> transactions;
        
        // Ưu tiên filter theo thứ tự: ngày > category > type > tất cả
        if (startDate != null && endDate != null) {
            transactions = transactionService.getByDateRange(userId, startDate, endDate);
        } else if (categoryId != null) {
            transactions = transactionService.getByCategory(userId, categoryId);
        } else if (type != null) {
            transactions = transactionService.getByType(userId, type);
        } else {
            transactions = transactionService.getAllByUser(userId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    /**
     * GET /api/transactions/{id}
     * Lấy giao dịch theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@PathVariable Long id) {
        
        TransactionResponse transaction = transactionService.getById(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }
    
    /**
     * PUT /api/transactions/{id}
     * Cập nhật giao dịch
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        
        TransactionResponse transaction = transactionService.update(getCurrentUserId(), id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật giao dịch thành công!", transaction));
    }
    
    /**
     * DELETE /api/transactions/{id}
     * Xóa giao dịch
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        
        transactionService.delete(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success("Xóa giao dịch thành công!"));
    }
}
