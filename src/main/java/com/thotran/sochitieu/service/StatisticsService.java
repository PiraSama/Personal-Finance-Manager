package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.response.CategoryChartResponse;
import com.thotran.sochitieu.dto.response.DailyStatisticsResponse;
import com.thotran.sochitieu.dto.response.MonthComparisonResponse;
import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.Transaction;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho Statistics/Reports.
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Thống kê chi tiêu theo category trong khoảng thời gian
     */
    public List<CategoryChartResponse> getByCategory(
            Long userId, LocalDate startDate, LocalDate endDate) {
        
        // Tính tổng chi tiêu để tính %
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate);
        
        // Lấy tất cả categories của user
        List<Category> categories = categoryRepository.findByUserId(userId);
        
        List<CategoryChartResponse> result = new ArrayList<>();
        
        for (Category category : categories) {
            BigDecimal amount = transactionRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    userId, category.getId(), startDate, endDate);
            
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
        
        result.sort(Comparator.comparing(CategoryChartResponse::getAmount).reversed());
        return result;
    }
    
    /**
     * Thống kê theo ngày trong tháng (cho calendar view)
     */
    public List<DailyStatisticsResponse> getDailyStats(Long userId, Integer month, Integer year) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Lấy tất cả giao dịch trong tháng
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, startOfMonth, endOfMonth);
        
        // Group theo ngày
        Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionDate));
        
        List<DailyStatisticsResponse> result = new ArrayList<>();
        
        // Tạo response cho từng ngày trong tháng
        for (int day = 1; day <= endOfMonth.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<Transaction> dayTransactions = groupedByDate.getOrDefault(date, new ArrayList<>());
            
            BigDecimal income = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal expense = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            result.add(DailyStatisticsResponse.builder()
                    .day(day)
                    .date(date.format(formatter))
                    .income(income)
                    .expense(expense)
                    .transactionCount((long) dayTransactions.size())
                    .build());
        }
        
        return result;
    }
    
    /**
     * So sánh 2 tháng
     */
    public MonthComparisonResponse compareMonths(
            Long userId, Integer month1, Integer year1, Integer month2, Integer year2) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        
        // Tháng 1
        LocalDate start1 = LocalDate.of(year1, month1, 1);
        LocalDate end1 = start1.withDayOfMonth(start1.lengthOfMonth());
        BigDecimal income1 = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, start1, end1);
        BigDecimal expense1 = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, start1, end1);
        
        // Tháng 2
        LocalDate start2 = LocalDate.of(year2, month2, 1);
        LocalDate end2 = start2.withDayOfMonth(start2.lengthOfMonth());
        BigDecimal income2 = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, start2, end2);
        BigDecimal expense2 = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, start2, end2);
        
        // Tính chênh lệch
        BigDecimal incomeDiff = income2.subtract(income1);
        BigDecimal expenseDiff = expense2.subtract(expense1);
        BigDecimal balance1 = income1.subtract(expense1);
        BigDecimal balance2 = income2.subtract(expense2);
        BigDecimal balanceDiff = balance2.subtract(balance1);
        
        // Tính % thay đổi
        Double incomeChangePercent = calculateChangePercent(income1, income2);
        Double expenseChangePercent = calculateChangePercent(expense1, expense2);
        
        return MonthComparisonResponse.builder()
                .month1(start1.format(formatter))
                .income1(income1)
                .expense1(expense1)
                .balance1(balance1)
                .month2(start2.format(formatter))
                .income2(income2)
                .expense2(expense2)
                .balance2(balance2)
                .incomeDiff(incomeDiff)
                .expenseDiff(expenseDiff)
                .balanceDiff(balanceDiff)
                .incomeChangePercent(incomeChangePercent)
                .expenseChangePercent(expenseChangePercent)
                .build();
    }
    
    private Double calculateChangePercent(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return newValue.subtract(oldValue)
                .divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
