package com.thotran.sochitieu.service;

import com.thotran.sochitieu.entity.Category;
import com.thotran.sochitieu.entity.TransactionType;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service tạo các danh mục mặc định cho user mới đăng ký.
 */
@Service
@RequiredArgsConstructor
public class DefaultCategoryInitializer {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Tạo tất cả danh mục mặc định cho user
     */
    public void createDefaultCategories(User user) {
        List<Category> defaultCategories = Arrays.asList(
            // === EXPENSE Categories (Chi tiêu) ===
            buildCategory("Ăn uống", TransactionType.EXPENSE, "🍔", "#FF6B6B", user),
            buildCategory("Di chuyển", TransactionType.EXPENSE, "🚗", "#4ECDC4", user),
            buildCategory("Mua sắm", TransactionType.EXPENSE, "🛒", "#45B7D1", user),
            buildCategory("Tiền nhà", TransactionType.EXPENSE, "🏠", "#96CEB4", user),
            buildCategory("Điện nước", TransactionType.EXPENSE, "💡", "#FFEAA7", user),
            buildCategory("Giải trí", TransactionType.EXPENSE, "🎬", "#DDA0DD", user),
            buildCategory("Sức khỏe", TransactionType.EXPENSE, "💊", "#98D8C8", user),
            buildCategory("Giáo dục", TransactionType.EXPENSE, "📚", "#F7DC6F", user),
            buildCategory("Chi tiêu khác", TransactionType.EXPENSE, "📦", "#BDC3C7", user),
            
            // === INCOME Categories (Thu nhập) ===
            buildCategory("Lương", TransactionType.INCOME, "💰", "#2ECC71", user),
            buildCategory("Thưởng", TransactionType.INCOME, "🎁", "#F39C12", user),
            buildCategory("Đầu tư", TransactionType.INCOME, "📈", "#3498DB", user),
            buildCategory("Thu nhập khác", TransactionType.INCOME, "💵", "#1ABC9C", user)
        );
        
        categoryRepository.saveAll(defaultCategories);
    }
    
    /**
     * Helper method để tạo Category object
     */
    private Category buildCategory(String name, TransactionType type, 
                                   String icon, String color, User user) {
        return Category.builder()
                .name(name)
                .type(type)
                .icon(icon)
                .color(color)
                .user(user)
                .build();
    }
}
