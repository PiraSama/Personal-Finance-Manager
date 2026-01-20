package com.thotran.sochitieu.repository;

import com.thotran.sochitieu.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Budget entity.
 * Cung cấp các phương thức truy vấn ngân sách.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    // Lấy tất cả budget của user trong một tháng/năm
    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    
    // Lấy budget theo category, tháng, năm (để kiểm tra trùng lặp)
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, Integer month, Integer year);
    
    // Tìm budget theo ID và user ID
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    
    // Kiểm tra budget đã tồn tại chưa
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, Integer month, Integer year);
    
    // Kiểm tra budget đã tồn tại (trừ ID hiện tại - dùng khi update)
    boolean existsByUserIdAndCategoryIdAndMonthAndYearAndIdNot(
            Long userId, Long categoryId, Integer month, Integer year, Long id);
}
