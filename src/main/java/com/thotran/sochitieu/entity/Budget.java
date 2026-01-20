package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho ngân sách hàng tháng theo danh mục.
 * Cho phép user đặt hạn mức chi tiêu cho từng danh mục mỗi tháng.
 */
@Entity
@Table(name = "budgets", 
       // Đảm bảo mỗi category chỉ có 1 budget trong 1 tháng
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"user_id", "category_id", "month", "year"}
       ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Số tiền ngân sách đặt ra
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    // Tháng (1-12)
    @Column(nullable = false)
    private Integer month;
    
    // Năm (VD: 2024, 2025)
    @Column(nullable = false)
    private Integer year;
    
    // Budget cho danh mục nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;
    
    // Budget thuộc user nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
