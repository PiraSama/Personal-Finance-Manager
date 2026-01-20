package com.thotran.sochitieu.repository;

import com.thotran.sochitieu.entity.Transaction;
import com.thotran.sochitieu.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Transaction entity.
 * Cung cấp các phương thức truy vấn giao dịch.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Lấy tất cả giao dịch của một user (sắp xếp theo ngày mới nhất)
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    
    // Lấy giao dịch theo khoảng thời gian
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    // Lấy giao dịch theo danh mục
    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(
            Long userId, Long categoryId);
    
    // Lấy giao dịch theo loại (INCOME/EXPENSE)
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(
            Long userId, TransactionType type);
    
    // Tìm giao dịch theo ID và user ID
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    // Tính tổng chi tiêu theo danh mục trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndCategoryIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Tính tổng thu/chi trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
