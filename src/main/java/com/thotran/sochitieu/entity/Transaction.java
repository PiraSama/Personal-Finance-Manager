package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một giao dịch thu/chi.
 * Mỗi giao dịch thuộc về một User và một Category.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Số tiền giao dịch (sử dụng BigDecimal để tính toán chính xác)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    // Loại giao dịch: INCOME hoặc EXPENSE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    // Mô tả giao dịch (VD: "Ăn phở sáng", "Lương tháng 1")
    private String description;
    
    // Ngày thực hiện giao dịch
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    // Giao dịch thuộc danh mục nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;
    
    // Giao dịch thuộc user nào
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
        // Nếu chưa set ngày, mặc định là ngày hiện tại
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
