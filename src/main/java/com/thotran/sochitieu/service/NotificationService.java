package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.response.NotificationResponse;
import com.thotran.sochitieu.entity.Notification;
import com.thotran.sochitieu.entity.NotificationType;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho Notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    
    // === QUERY METHODS ===
    
    /**
     * Lấy tất cả thông báo của user
     */
    public List<NotificationResponse> getAllByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thông báo chưa đọc
     */
    public List<NotificationResponse> getUnread(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Đếm số thông báo chưa đọc
     */
    public Long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Đánh dấu 1 thông báo là đã đọc
     */
    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, 
                        "Không tìm thấy thông báo"));
        
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        
        return mapToResponse(notification);
    }
    
    /**
     * Đánh dấu tất cả là đã đọc
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }
    
    /**
     * Xóa thông báo
     */
    @Transactional
    public void delete(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                        "Không tìm thấy thông báo"));
        
        notificationRepository.delete(notification);
    }
    
    // === CREATE NOTIFICATION METHODS ===
    
    /**
     * Gửi thông báo chào mừng user mới
     */
    @Transactional
    public void sendWelcomeNotification(User user) {
        Notification notification = Notification.builder()
                .type(NotificationType.WELCOME)
                .title("Chào mừng bạn đến với Sổ Chi Tiêu! 🎉")
                .message("Hãy bắt đầu ghi chép chi tiêu hàng ngày để quản lý tài chính tốt hơn nhé!")
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    /**
     * Gửi thông báo cảnh báo ngân sách (>80%)
     */
    @Transactional
    public void sendBudgetWarning(User user, String categoryName, 
                                   BigDecimal spent, BigDecimal budget, Double percentUsed) {
        String extraData = String.format("{\"categoryName\":\"%s\",\"spent\":%s,\"budget\":%s,\"percent\":%.1f}",
                categoryName, spent, budget, percentUsed);
        
        Notification notification = Notification.builder()
                .type(NotificationType.BUDGET_WARNING)
                .title("⚠️ Cảnh báo ngân sách: " + categoryName)
                .message(String.format("Bạn đã chi tiêu %.1f%% ngân sách cho %s. Hãy cân nhắc tiết kiệm hơn!", 
                        percentUsed, categoryName))
                .extraData(extraData)
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    /**
     * Gửi thông báo đã vượt ngân sách
     */
    @Transactional
    public void sendBudgetExceeded(User user, String categoryName,
                                    BigDecimal spent, BigDecimal budget) {
        BigDecimal exceeded = spent.subtract(budget);
        String extraData = String.format("{\"categoryName\":\"%s\",\"exceeded\":%s}",
                categoryName, exceeded);
        
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
    
    /**
     * Gửi thông báo giao dịch định kỳ đến hạn
     */
    @Transactional
    public void sendRecurringDue(User user, String description, BigDecimal amount) {
        Notification notification = Notification.builder()
                .type(NotificationType.RECURRING_DUE)
                .title("📅 Giao dịch định kỳ đến hạn")
                .message(String.format("'%s' với số tiền %s sẽ được ghi nhận hôm nay.",
                        description, formatCurrency(amount)))
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    /**
     * Gửi thông báo nhắc nhở ghi chép
     */
    @Transactional
    public void sendReminder(User user) {
        Notification notification = Notification.builder()
                .type(NotificationType.REMINDER)
                .title("📝 Nhắc nhở ghi chép")
                .message("Đừng quên ghi lại các khoản chi tiêu trong ngày hôm nay nhé!")
                .user(user)
                .build();
        
        notificationRepository.save(notification);
    }
    
    // === HELPER METHODS ===
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .typeDisplay(getTypeDisplay(notification.getType()))
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .extraData(notification.getExtraData())
                .createdAt(notification.getCreatedAt())
                .timeAgo(getTimeAgo(notification.getCreatedAt()))
                .build();
    }
    
    private String getTypeDisplay(NotificationType type) {
        return switch (type) {
            case WELCOME -> "Chào mừng";
            case BUDGET_WARNING -> "Cảnh báo ngân sách";
            case BUDGET_EXCEEDED -> "Vượt ngân sách";
            case RECURRING_DUE -> "Giao dịch định kỳ";
            case RECURRING_PROCESSED -> "Giao dịch tự động";
            case REMINDER -> "Nhắc nhở";
            case INFO -> "Thông tin";
        };
    }
    
    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        
        if (seconds < 60) return "Vừa xong";
        if (seconds < 3600) return (seconds / 60) + " phút trước";
        if (seconds < 86400) return (seconds / 3600) + " giờ trước";
        if (seconds < 604800) return (seconds / 86400) + " ngày trước";
        return (seconds / 604800) + " tuần trước";
    }
    
    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f đ", amount);
    }
}
