package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.BudgetRequest;
import com.thotran.sochitieu.dto.response.BudgetResponse;
import com.thotran.sochitieu.entity.Budget;
import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.BudgetRepository;
import com.thotran.sochitieu.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ liên quan đến Budget (Ngân sách).
 */
@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    
    /**
     * Tạo ngân sách mới
     */
    @Transactional
    public BudgetResponse create(Long userId, BudgetRequest request) {
        User user = userService.getUserEntity(userId);
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        
        // Kiểm tra đã có budget cho category này trong tháng chưa
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear())) {
            throw new AppException(ErrorCode.BUDGET_EXISTED);
        }
        
        Budget budget = Budget.builder()
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .category(category)
                .user(user)
                .build();
        
        budget = budgetRepository.save(budget);
        
        return mapToResponse(budget, userId);
    }
    
    /**
     * Lấy tất cả budget của user trong một tháng
     */
    public List<BudgetResponse> getByMonth(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(budget -> mapToResponse(budget, userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy budget theo ID
     */
    public BudgetResponse getById(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
        
        return mapToResponse(budget, userId);
    }
    
    /**
     * Cập nhật ngân sách
     */
    @Transactional
    public BudgetResponse update(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
        
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        
        // Kiểm tra có budget khác cho category+month+year này không
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYearAndIdNot(
                userId, request.getCategoryId(), request.getMonth(), request.getYear(), budgetId)) {
            throw new AppException(ErrorCode.BUDGET_EXISTED);
        }
        
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCategory(category);
        
        budget = budgetRepository.save(budget);
        
        return mapToResponse(budget, userId);
    }
    
    /**
     * Xóa ngân sách
     */
    @Transactional
    public void delete(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
        
        budgetRepository.delete(budget);
    }
    
    // === Helper method: Convert Entity -> DTO (có tính toán spent) ===
    private BudgetResponse mapToResponse(Budget budget, Long userId) {
        // Tính ngày đầu và cuối tháng
        LocalDate startOfMonth = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        
        // Tính tổng đã chi cho category trong tháng này
        BigDecimal spent = transactionRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                userId, budget.getCategory().getId(), startOfMonth, endOfMonth);
        
        // Tính số còn lại và phần trăm
        BigDecimal remaining = budget.getAmount().subtract(spent);
        Double percentUsed = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        
        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .spent(spent)
                .remaining(remaining)
                .percentUsed(percentUsed)
                .month(budget.getMonth())
                .year(budget.getYear())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryIcon(budget.getCategory().getIcon())
                .categoryColor(budget.getCategory().getColor())
                .build();
    }
}
