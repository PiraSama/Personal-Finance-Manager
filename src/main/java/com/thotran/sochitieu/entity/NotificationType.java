package com.thotran.sochitieu.entity;

/**
 * Enum định nghĩa các loại thông báo.
 */
public enum NotificationType {
    // === Thông báo hệ thống ===
    WELCOME,                    // Chào mừng user mới
    
    // === Thông báo ngân sách ===
    BUDGET_WARNING,             // Cảnh báo sắp vượt ngân sách (>80%)
    BUDGET_EXCEEDED,            // Đã vượt ngân sách
    
    // === Thông báo recurring ===
    RECURRING_DUE,              // Giao dịch định kỳ đến hạn
    RECURRING_PROCESSED,        // Giao dịch định kỳ đã được xử lý
    
    // === Thông báo chung ===
    REMINDER,                   // Nhắc nhở ghi chép chi tiêu
    INFO                        // Thông tin chung
}
