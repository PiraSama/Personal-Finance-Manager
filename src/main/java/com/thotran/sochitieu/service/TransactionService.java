package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.TransactionRequest;
import com.thotran.sochitieu.dto.response.TransactionResponse;
import com.thotran.sochitieu.entity.*;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.BudgetRepository;
import com.thotran.sochitieu.repository.NotificationRepository;
import com.thotran.sochitieu.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ liên quan đến Transaction (Giao dịch).
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BudgetRepository budgetRepository;
    private final NotificationRepository notificationRepository;
    
    // Ngưỡng cảnh báo (80%)
    private static final double WARNING_THRESHOLD = 80.0;
    
    /**
     * Tạo giao dịch mới
     */
    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest request) {
        User user = userService.getUserEntity(userId);
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate() != null 
                        ? request.getTransactionDate() 
                        : LocalDate.now())
                .category(category)
                .user(user)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        // Kiểm tra ngân sách nếu là giao dịch chi tiêu
        if (request.getType() == TransactionType.EXPENSE) {
            checkBudgetAndNotify(user, category, transaction.getTransactionDate());
        }
        
        return mapToResponse(transaction);
    }
    
    /**
     * Lấy tất cả giao dịch của user
     */
    public List<TransactionResponse> getAllByUser(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy giao dịch theo khoảng thời gian
     */
    public List<TransactionResponse> getByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy giao dịch theo danh mục
     */
    public List<TransactionResponse> getByCategory(Long userId, Long categoryId) {
        return transactionRepository
                .findByUserIdAndCategoryIdOrderByTransactionDateDesc(userId, categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy giao dịch theo loại (INCOME/EXPENSE)
     */
    public List<TransactionResponse> getByType(Long userId, TransactionType type) {
        return transactionRepository
                .findByUserIdAndTypeOrderByTransactionDateDesc(userId, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy giao dịch theo ID
     */
    public TransactionResponse getById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        
        return mapToResponse(transaction);
    }
    
    /**
     * Cập nhật giao dịch
     */
    @Transactional
    public TransactionResponse update(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        User user = transaction.getUser();
        
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate() != null 
                ? request.getTransactionDate() 
                : transaction.getTransactionDate());
        transaction.setCategory(category);
        
        transaction = transactionRepository.save(transaction);
        
        // Kiểm tra ngân sách nếu là giao dịch chi tiêu
        if (request.getType() == TransactionType.EXPENSE) {
            checkBudgetAndNotify(user, category, transaction.getTransactionDate());
        }
        
        return mapToResponse(transaction);
    }
    
    /**
     * Xóa giao dịch
     */
    @Transactional
    public void delete(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        
        transactionRepository.delete(transaction);
    }
    
    // === Kiểm tra ngân sách và gửi thông báo ===
    private void checkBudgetAndNotify(User user, Category category, LocalDate transactionDate) {
        int month = transactionDate.getMonthValue();
        int year = transactionDate.getYear();
        
        // Tìm budget của category trong tháng này
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(), category.getId(), month, year);
        
        if (budgetOpt.isEmpty()) {
            return; // Không có budget -> không cần kiểm tra
        }
        
        Budget budget = budgetOpt.get();
        
        // Tính tổng đã chi trong tháng cho category này
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        
        BigDecimal spent = transactionRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                user.getId(), category.getId(), startOfMonth, endOfMonth);
        
        BigDecimal budgetAmount = budget.getAmount();
        
        // Tính phần trăm đã chi
        double percentUsed = spent.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        
        // Gửi thông báo tùy theo mức độ
        if (spent.compareTo(budgetAmount) > 0) {
            // Đã vượt ngân sách
            sendBudgetExceededNotification(user, category.getName(), spent, budgetAmount);
        } else if (percentUsed >= WARNING_THRESHOLD) {
            // Sắp vượt ngân sách (>= 80%)
            sendBudgetWarningNotification(user, category.getName(), spent, budgetAmount, percentUsed);
        }
    }
    
    private void sendBudgetWarningNotification(User user, String categoryName,
                                                BigDecimal spent, BigDecimal budget, double percentUsed) {
        String extraData = String.format(
                "{\"categoryName\":\"%s\",\"spent\":%s,\"budget\":%s,\"percent\":%.1f}",
                categoryName, spent, budget, percentUsed);
        
        Notification notification = Notification.builder()
                .type(NotificationType.BUDGET_WARNING)
                .title("⚠️ Cảnh báo ngân sách: " + categoryName)
                .message(String.format("Bạn đã chi tiêu %.1f%% ngân sách cho %s (%s/%s). Hãy cân nhắc tiết kiệm!",
                        percentUsed, categoryName, formatCurrency(spent), formatCurrency(budget)))
                .extraData(extraData)
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    private void sendBudgetExceededNotification(User user, String categoryName,
                                                 BigDecimal spent, BigDecimal budget) {
        BigDecimal exceeded = spent.subtract(budget);
        String extraData = String.format(
                "{\"categoryName\":\"%s\",\"exceeded\":%s}", categoryName, exceeded);
        
        Notification notification = Notification.builder()
                .type(NotificationType.BUDGET_EXCEEDED)
                .title("🚨 Vượt ngân sách: " + categoryName)
                .message(String.format("Bạn đã vượt ngân sách %s với số tiền %s!",
                        categoryName, formatCurrency(exceeded)))
                .extraData(extraData)
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f đ", amount);
    }
    
    // === Helper method: Convert Entity -> DTO ===
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryIcon(transaction.getCategory().getIcon())
                .categoryColor(transaction.getCategory().getColor())
                .build();
    }
}

