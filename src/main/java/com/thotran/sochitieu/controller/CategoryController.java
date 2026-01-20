package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.CategoryRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.CategoryResponse;
import com.thotran.sochitieu.entity.TransactionType;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Category APIs.
 * Base path: /api/categories
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * POST /api/categories
     * Tạo danh mục mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CategoryRequest request) {
        
        CategoryResponse category = categoryService.create(getCurrentUserId(), request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo danh mục thành công!", category));
    }
    
    /**
     * GET /api/categories
     * Lấy tất cả danh mục của user
     * 
     * @param type (optional) Lọc theo loại: INCOME hoặc EXPENSE
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @RequestParam(required = false) TransactionType type) {
        
        List<CategoryResponse> categories;
        
        if (type != null) {
            categories = categoryService.getByUserAndType(getCurrentUserId(), type);
        } else {
            categories = categoryService.getAllByUser(getCurrentUserId());
        }
        
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    /**
     * GET /api/categories/{id}
     * Lấy danh mục theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        
        CategoryResponse category = categoryService.getById(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    /**
     * PUT /api/categories/{id}
     * Cập nhật danh mục
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        
        CategoryResponse category = categoryService.update(getCurrentUserId(), id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công!", category));
    }
    
    /**
     * DELETE /api/categories/{id}
     * Xóa danh mục
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        
        categoryService.delete(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success("Xóa danh mục thành công!"));
    }
}
