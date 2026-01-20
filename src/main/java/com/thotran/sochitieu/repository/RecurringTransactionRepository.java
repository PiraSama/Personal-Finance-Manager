package com.thotran.sochitieu.repository;

import com.thotran.sochitieu.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho RecurringTransaction entity.
 */
@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    
    // Lấy tất cả recurring transactions của user
    List<RecurringTransaction> findByUserId(Long userId);
    
    // Lấy theo user và trạng thái
    List<RecurringTransaction> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    // Tìm theo ID và user ID
    Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);
    
    // Lấy các recurring đang active và đến hạn (nextDueDate <= today)
    List<RecurringTransaction> findByIsActiveTrueAndNextDueDateLessThanEqual(LocalDate date);
    
    // Lấy theo user và đến hạn trong tháng
    List<RecurringTransaction> findByUserIdAndIsActiveTrueAndNextDueDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);
}
