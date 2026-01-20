package com.thotran.sochitieu.repository;

import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Category entity.
 * Cung cấp các phương thức truy vấn danh mục.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Lấy tất cả danh mục của một user
    List<Category> findByUserId(Long userId);
    
    // Lấy danh mục theo user và loại (INCOME/EXPENSE)
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    
    // Tìm danh mục theo ID và user ID (đảm bảo user chỉ truy cập danh mục của mình)
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    
    // Kiểm tra tên danh mục đã tồn tại cho user chưa
    boolean existsByNameAndUserId(String name, Long userId);
    
    // Kiểm tra tên danh mục đã tồn tại cho user (trừ ID hiện tại - dùng khi update)
    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);
}
