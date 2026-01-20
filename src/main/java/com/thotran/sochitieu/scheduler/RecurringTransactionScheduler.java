package com.thotran.sochitieu.scheduler;

import com.thotran.sochitieu.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled Job để xử lý recurring transactions.
 * Chạy hàng ngày lúc 00:05 để tạo transactions cho các recurring đến hạn.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionScheduler {
    
    private final RecurringTransactionService recurringService;
    
    /**
     * Chạy hàng ngày lúc 00:05
     * Cron format: second minute hour dayOfMonth month dayOfWeek
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void processRecurringTransactions() {
        log.info("=== Starting recurring transaction processing at {} ===", 
                LocalDateTime.now());
        
        try {
            int processedCount = recurringService.processRecurringTransactions();
            log.info("=== Processed {} recurring transactions ===", processedCount);
        } catch (Exception e) {
            log.error("Error processing recurring transactions", e);
        }
    }
    
    /**
     * Endpoint để trigger thủ công (chỉ dùng cho testing)
     * Có thể gọi từ Controller nếu cần
     */
    public int triggerManually() {
        log.info("Manual trigger for recurring transaction processing");
        return recurringService.processRecurringTransactions();
    }
}
