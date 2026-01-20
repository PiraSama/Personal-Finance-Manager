package com.thotran.sochitieu.dto.response;

import com.thotran.sochitieu.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin notification.
 */
@Data
@Builder
public class NotificationResponse {
    
    private Long id;
    private NotificationType type;
    private String typeDisplay;         // Human readable: "Cảnh báo ngân sách"
    private String title;
    private String message;
    private Boolean isRead;
    private String extraData;           // JSON data (optional)
    private LocalDateTime createdAt;
    private String timeAgo;             // "5 phút trước", "2 giờ trước"
}
