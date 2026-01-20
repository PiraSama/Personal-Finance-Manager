package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.BudgetRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.BudgetResponse;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller cho Budget APIs.
 * Base path: /api/budgets
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    
    private final BudgetService budgetService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * POST /api/budgets
     * Tạo ngân sách mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> create(
            @Valid @RequestBody BudgetRequest request) {
        
        BudgetResponse budget = budgetService.create(getCurrentUserId(), request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo ngân sách thành công!", budget));
    }
    
    /**
     * GET /api/budgets
     * Lấy danh sách ngân sách theo tháng/năm
     * 
     * @param month Tháng (1-12), mặc định là tháng hiện tại
     * @param year Năm, mặc định là năm hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getByMonth(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        // Mặc định lấy tháng/năm hiện tại nếu không truyền
        LocalDate now = LocalDate.now();
        int queryMonth = month != null ? month : now.getMonthValue();
        int queryYear = year != null ? year : now.getYear();
        
        List<BudgetResponse> budgets = budgetService.getByMonth(
                getCurrentUserId(), queryMonth, queryYear);
        
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }
    
    /**
     * GET /api/budgets/{id}
     * Lấy ngân sách theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getById(@PathVariable Long id) {
        
        BudgetResponse budget = budgetService.getById(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success(budget));
    }
    
    /**
     * PUT /api/budgets/{id}
     * Cập nhật ngân sách
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        
        BudgetResponse budget = budgetService.update(getCurrentUserId(), id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật ngân sách thành công!", budget));
    }
    
    /**
     * DELETE /api/budgets/{id}
     * Xóa ngân sách
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        
        budgetService.delete(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success("Xóa ngân sách thành công!"));
    }
}
