package com.thotran.sochitieu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho người dùng của ứng dụng.
 * Mỗi user có thể có nhiều Category, Transaction và Budget.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Email đăng nhập (unique)
    @Column(nullable = false, unique = true)
    private String email;
    
    // Mật khẩu đã mã hóa
    @Column(nullable = false)
    private String password;
    
    // Tên hiển thị
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    // Vai trò: USER hoặc ADMIN
    @Enumerated(EnumType.STRING)
    private Role role;
    
    // Danh sách danh mục của user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Category> categories = new ArrayList<>();
    
    // Danh sách giao dịch của user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Transaction> transactions = new ArrayList<>();
    
    // Danh sách ngân sách của user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
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
        if (role == null) {
            role = Role.USER;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
