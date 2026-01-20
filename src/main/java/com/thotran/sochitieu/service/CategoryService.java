package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.CategoryRequest;
import com.thotran.sochitieu.dto.response.CategoryResponse;
import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.TransactionType;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ liên quan đến Category (Danh mục).
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    
    /**
     * Tạo danh mục mới
     */
    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest request) {
        User user = userService.getUserEntity(userId);
        
        // Kiểm tra tên danh mục đã tồn tại chưa
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .color(request.getColor())
                .user(user)
                .build();
        
        category = categoryRepository.save(category);
        
        return mapToResponse(category);
    }
    
    /**
     * Lấy tất cả danh mục của user
     */
    public List<CategoryResponse> getAllByUser(Long userId) {
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh mục theo loại (INCOME/EXPENSE)
     */
    public List<CategoryResponse> getByUserAndType(Long userId, TransactionType type) {
        return categoryRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh mục theo ID
     */
    public CategoryResponse getById(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        return mapToResponse(category);
    }
    
    /**
     * Lấy category entity theo ID (internal use)
     */
    public Category getCategoryEntity(Long userId, Long categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }
    
    /**
     * Cập nhật danh mục
     */
    @Transactional
    public CategoryResponse update(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // Kiểm tra tên mới có trùng với danh mục khác không
        if (categoryRepository.existsByNameAndUserIdAndIdNot(request.getName(), userId, categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }
        
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        
        category = categoryRepository.save(category);
        
        return mapToResponse(category);
    }
    
    /**
     * Xóa danh mục
     */
    @Transactional
    public void delete(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        categoryRepository.delete(category);
    }
    
    // === Helper method: Convert Entity -> DTO ===
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .build();
    }
}
