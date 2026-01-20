package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.RecurringTransactionRequest;
import com.thotran.sochitieu.dto.response.RecurringTransactionResponse;
import com.thotran.sochitieu.entity.*;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.RecurringTransactionRepository;
import com.thotran.sochitieu.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho Recurring Transactions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionService {
    
    private final RecurringTransactionRepository recurringRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    
    /**
     * Tạo recurring transaction mới
     */
    @Transactional
    public RecurringTransactionResponse create(Long userId, RecurringTransactionRequest request) {
        User user = userService.getUserEntity(userId);
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        
        RecurringTransaction recurring = RecurringTransaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .description(request.getDescription())
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .nextDueDate(request.getStartDate())
                .isActive(true)
                .category(category)
                .user(user)
                .build();
        
        recurring = recurringRepository.save(recurring);
        
        return mapToResponse(recurring);
    }
    
    /**
     * Lấy tất cả recurring transactions của user
     */
    public List<RecurringTransactionResponse> getAllByUser(Long userId) {
        return recurringRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy theo trạng thái active
     */
    public List<RecurringTransactionResponse> getByActive(Long userId, Boolean isActive) {
        return recurringRepository.findByUserIdAndIsActive(userId, isActive)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các recurring đến hạn trong tháng
     */
    public List<RecurringTransactionResponse> getUpcoming(Long userId, Integer month, Integer year) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        
        return recurringRepository
                .findByUserIdAndIsActiveTrueAndNextDueDateBetween(userId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy recurring theo ID
     */
    public RecurringTransactionResponse getById(Long userId, Long recurringId) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND, 
                        "Không tìm thấy giao dịch định kỳ"));
        
        return mapToResponse(recurring);
    }
    
    /**
     * Cập nhật recurring transaction
     */
    @Transactional
    public RecurringTransactionResponse update(Long userId, Long recurringId, 
                                                RecurringTransactionRequest request) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND,
                        "Không tìm thấy giao dịch định kỳ"));
        
        Category category = categoryService.getCategoryEntity(userId, request.getCategoryId());
        
        recurring.setAmount(request.getAmount());
        recurring.setType(request.getType());
        recurring.setDescription(request.getDescription());
        recurring.setFrequency(request.getFrequency());
        recurring.setStartDate(request.getStartDate());
        recurring.setEndDate(request.getEndDate());
        recurring.setCategory(category);
        
        recurring = recurringRepository.save(recurring);
        
        return mapToResponse(recurring);
    }
    
    /**
     * Kích hoạt/Tạm dừng recurring
     */
    @Transactional
    public RecurringTransactionResponse toggleActive(Long userId, Long recurringId) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND,
                        "Không tìm thấy giao dịch định kỳ"));
        
        recurring.setIsActive(!recurring.getIsActive());
        recurring = recurringRepository.save(recurring);
        
        return mapToResponse(recurring);
    }
    
    /**
     * Xóa recurring transaction
     */
    @Transactional
    public void delete(Long userId, Long recurringId) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND,
                        "Không tìm thấy giao dịch định kỳ"));
        
        recurringRepository.delete(recurring);
    }
    
    /**
     * Xử lý các recurring đến hạn - tạo transaction thực và cập nhật nextDueDate
     * Method này được gọi bởi Scheduled Job
     */
    @Transactional
    public int processRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueRecurrings = 
                recurringRepository.findByIsActiveTrueAndNextDueDateLessThanEqual(today);
        
        int processedCount = 0;
        
        for (RecurringTransaction recurring : dueRecurrings) {
            // Kiểm tra endDate
            if (recurring.getEndDate() != null && today.isAfter(recurring.getEndDate())) {
                recurring.setIsActive(false);
                recurringRepository.save(recurring);
                continue;
            }
            
            // Tạo transaction thực
            Transaction transaction = Transaction.builder()
                    .amount(recurring.getAmount())
                    .type(recurring.getType())
                    .description(recurring.getDescription() + " (Tự động)")
                    .transactionDate(recurring.getNextDueDate())
                    .category(recurring.getCategory())
                    .user(recurring.getUser())
                    .build();
            
            transactionRepository.save(transaction);
            
            // Cập nhật nextDueDate
            recurring.setNextDueDate(recurring.calculateNextDueDate());
            recurringRepository.save(recurring);
            
            processedCount++;
            log.info("Processed recurring transaction ID: {}, Amount: {}", 
                    recurring.getId(), recurring.getAmount());
        }
        
        return processedCount;
    }
    
    // === Helper methods ===
    
    private RecurringTransactionResponse mapToResponse(RecurringTransaction recurring) {
        return RecurringTransactionResponse.builder()
                .id(recurring.getId())
                .amount(recurring.getAmount())
                .type(recurring.getType())
                .description(recurring.getDescription())
                .frequency(recurring.getFrequency())
                .frequencyDisplay(getFrequencyDisplay(recurring.getFrequency()))
                .startDate(recurring.getStartDate())
                .endDate(recurring.getEndDate())
                .nextDueDate(recurring.getNextDueDate())
                .isActive(recurring.getIsActive())
                .categoryId(recurring.getCategory().getId())
                .categoryName(recurring.getCategory().getName())
                .categoryIcon(recurring.getCategory().getIcon())
                .categoryColor(recurring.getCategory().getColor())
                .build();
    }
    
    private String getFrequencyDisplay(RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> "Hàng ngày";
            case WEEKLY -> "Hàng tuần";
            case MONTHLY -> "Hàng tháng";
            case YEARLY -> "Hàng năm";
        };
    }
}
