package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.response.CategoryChartResponse;
import com.thotran.sochitieu.dto.response.DashboardSummaryResponse;
import com.thotran.sochitieu.dto.response.DashboardSummaryResponse.CategoryExpenseResponse;
import com.thotran.sochitieu.dto.response.MonthlyTrendResponse;
import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.TransactionType;
import com.thotran.sochitieu.repository.CategoryRepository;
import com.thotran.sochitieu.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho Dashboard.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Lấy tổng quan tài chính của tháng hiện tại
     */
    public DashboardSummaryResponse getSummary(Long userId, Integer month, Integer year) {
        // Xác định khoảng thời gian
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        
        // Tính tổng thu
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        
        // Tính tổng chi
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);
        
        // Tính số dư
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        // Tính tỷ lệ tiết kiệm
        Double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = balance.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        
        // Đếm số giao dịch
        Long transactionCount = (long) transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, startOfMonth, endOfMonth).size();
        
        // Lấy top 5 categories chi tiêu nhiều nhất
        List<CategoryExpenseResponse> topCategories = getTopExpenseCategories(
                userId, startOfMonth, endOfMonth, totalExpense, 5);
        
        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .savingsRate(savingsRate)
                .transactionCount(transactionCount)
                .topExpenseCategories(topCategories)
                .build();
    }
    
    /**
     * Lấy data cho biểu đồ chi tiêu theo category
     */
    public List<CategoryChartResponse> getExpenseByCategory(Long userId, Integer month, Integer year) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        
        // Lấy tổng chi tiêu để tính %
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);
        
        // Lấy tất cả categories loại EXPENSE của user
        List<Category> expenseCategories = categoryRepository.findByUserIdAndType(
                userId, TransactionType.EXPENSE);
        
        List<CategoryChartResponse> result = new ArrayList<>();
        
        for (Category category : expenseCategories) {
            BigDecimal amount = transactionRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    userId, category.getId(), startOfMonth, endOfMonth);
            
            // Chỉ thêm nếu có chi tiêu
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                Double percent = 0.0;
                if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                    percent = amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }
                
                result.add(CategoryChartResponse.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .categoryIcon(category.getIcon())
                        .categoryColor(category.getColor())
                        .amount(amount)
                        .percent(percent)
                        .build());
            }
        }
        
        // Sắp xếp theo số tiền giảm dần
        result.sort(Comparator.comparing(CategoryChartResponse::getAmount).reversed());
        
        return result;
    }
    
    /**
     * Lấy xu hướng thu/chi 6 tháng gần nhất
     */
    public List<MonthlyTrendResponse> getMonthlyTrend(Long userId, int numberOfMonths) {
        List<MonthlyTrendResponse> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        
        for (int i = numberOfMonths - 1; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            LocalDate startOfMonth = targetDate.withDayOfMonth(1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
            
            BigDecimal income = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.INCOME, startOfMonth, endOfMonth);
            
            BigDecimal expense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);
            
            result.add(MonthlyTrendResponse.builder()
                    .month(startOfMonth.format(formatter))
                    .monthValue(startOfMonth.getMonthValue())
                    .year(startOfMonth.getYear())
                    .income(income)
                    .expense(expense)
                    .balance(income.subtract(expense))
                    .build());
        }
        
        return result;
    }
    
    // === Helper method ===
    private List<CategoryExpenseResponse> getTopExpenseCategories(
            Long userId, LocalDate startDate, LocalDate endDate, 
            BigDecimal totalExpense, int limit) {
        
        List<Category> expenseCategories = categoryRepository.findByUserIdAndType(
                userId, TransactionType.EXPENSE);
        
        List<CategoryExpenseResponse> categoryExpenses = new ArrayList<>();
        
        for (Category category : expenseCategories) {
            BigDecimal amount = transactionRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    userId, category.getId(), startDate, endDate);
            
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                Double percent = 0.0;
                if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                    percent = amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }
                
                categoryExpenses.add(CategoryExpenseResponse.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .categoryIcon(category.getIcon())
                        .categoryColor(category.getColor())
                        .amount(amount)
                        .percent(percent)
                        .build());
            }
        }
        
        // Sắp xếp và lấy top
        return categoryExpenses.stream()
                .sorted(Comparator.comparing(CategoryExpenseResponse::getAmount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
