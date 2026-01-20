package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.RecurringTransactionRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.RecurringTransactionResponse;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.RecurringTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller cho Recurring Transaction APIs.
 * Base path: /api/recurring-transactions
 */
@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
public class RecurringTransactionController {
    
    private final RecurringTransactionService recurringService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * POST /api/recurring-transactions
     * Tạo recurring transaction mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> create(
            @Valid @RequestBody RecurringTransactionRequest request) {
        
        RecurringTransactionResponse recurring = recurringService.create(
                getCurrentUserId(), request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo giao dịch định kỳ thành công!", recurring));
    }
    
    /**
     * GET /api/recurring-transactions
     * Lấy tất cả recurring transactions
     * 
     * @param active (optional) Lọc theo trạng thái: true/false
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringTransactionResponse>>> getAll(
            @RequestParam(required = false) Boolean active) {
        
        List<RecurringTransactionResponse> list;
        
        if (active != null) {
            list = recurringService.getByActive(getCurrentUserId(), active);
        } else {
            list = recurringService.getAllByUser(getCurrentUserId());
        }
        
        return ResponseEntity.ok(ApiResponse.success(list));
    }
    
    /**
     * GET /api/recurring-transactions/upcoming
     * Lấy các recurring đến hạn trong tháng
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<RecurringTransactionResponse>>> getUpcoming(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int queryMonth = month != null ? month : now.getMonthValue();
        int queryYear = year != null ? year : now.getYear();
        
        List<RecurringTransactionResponse> list = recurringService.getUpcoming(
                getCurrentUserId(), queryMonth, queryYear);
        
        return ResponseEntity.ok(ApiResponse.success(list));
    }
    
    /**
     * GET /api/recurring-transactions/{id}
     * Lấy recurring theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> getById(
            @PathVariable Long id) {
        
        RecurringTransactionResponse recurring = recurringService.getById(
                getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success(recurring));
    }
    
    /**
     * PUT /api/recurring-transactions/{id}
     * Cập nhật recurring transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RecurringTransactionRequest request) {
        
        RecurringTransactionResponse recurring = recurringService.update(
                getCurrentUserId(), id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công!", recurring));
    }
    
    /**
     * PATCH /api/recurring-transactions/{id}/toggle
     * Kích hoạt/Tạm dừng recurring
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> toggle(
            @PathVariable Long id) {
        
        RecurringTransactionResponse recurring = recurringService.toggleActive(
                getCurrentUserId(), id);
        
        String message = recurring.getIsActive() ? "Đã kích hoạt!" : "Đã tạm dừng!";
        return ResponseEntity.ok(ApiResponse.success(message, recurring));
    }
    
    /**
     * DELETE /api/recurring-transactions/{id}
     * Xóa recurring transaction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        
        recurringService.delete(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công!"));
    }
}
