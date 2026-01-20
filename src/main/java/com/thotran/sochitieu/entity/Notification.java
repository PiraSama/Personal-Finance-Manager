package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho thông báo của user.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Loại thông báo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    // Tiêu đề thông báo
    @Column(nullable = false)
    private String title;
    
    // Nội dung thông báo
    @Column(length = 1000)
    private String message;
    
    // Đã đọc chưa
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;
    
    // Dữ liệu bổ sung (JSON) - VD: categoryId, budgetId, amount...
    @Column(name = "extra_data", length = 500)
    private String extraData;
    
    // Thông báo của user nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
