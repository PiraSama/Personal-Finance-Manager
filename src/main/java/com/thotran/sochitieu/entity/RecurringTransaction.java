package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho giao dịch lặp lại.
 * VD: Lương hàng tháng, tiền nhà, học phí, Netflix subscription...
 */
@Entity
@Table(name = "recurring_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Số tiền mỗi lần
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    // Loại giao dịch: INCOME hoặc EXPENSE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    // Mô tả (VD: "Lương tháng", "Tiền nhà")
    private String description;
    
    // Tần suất lặp lại
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurringFrequency frequency;
    
    // Ngày bắt đầu
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    // Ngày kết thúc (nullable = vô thời hạn)
    @Column(name = "end_date")
    private LocalDate endDate;
    
    // Ngày đến hạn tiếp theo
    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;
    
    // Đang hoạt động?
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    // Thuộc danh mục nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;
    
    // Thuộc user nào
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
        if (nextDueDate == null) {
            nextDueDate = startDate;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Tính ngày đến hạn tiếp theo dựa trên frequency
     */
    public LocalDate calculateNextDueDate() {
        return switch (frequency) {
            case DAILY -> nextDueDate.plusDays(1);
            case WEEKLY -> nextDueDate.plusWeeks(1);
            case MONTHLY -> nextDueDate.plusMonths(1);
            case YEARLY -> nextDueDate.plusYears(1);
        };
    }
}
