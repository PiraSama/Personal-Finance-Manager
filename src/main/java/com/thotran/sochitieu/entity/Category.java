package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho danh mục thu/chi.
 * Ví dụ: "Ăn uống", "Di chuyển", "Lương", "Thưởng",...
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Tên danh mục (VD: "Ăn uống", "Tiền nhà")
    @Column(nullable = false)
    private String name;
    
    // Loại danh mục: INCOME hoặc EXPENSE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    // Icon emoji hoặc tên icon (VD: "🍔", "fa-utensils")
    private String icon;
    
    // Mã màu hex (VD: "#FF5733")
    private String color;
    
    // Danh mục thuộc về user nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude  // Tránh circular reference khi toString
    private User user;
    
    // Danh sách giao dịch thuộc danh mục này
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<Transaction> transactions = new ArrayList<>();
    
    // Danh sách ngân sách thuộc danh mục này
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<Budget> budgets = new ArrayList<>();
    
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
