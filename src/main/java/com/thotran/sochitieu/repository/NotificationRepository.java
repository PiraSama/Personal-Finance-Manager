package com.thotran.sochitieu.repository;

import com.thotran.sochitieu.entity.Notification;
import com.thotran.sochitieu.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Lấy tất cả thông báo của user (mới nhất trước)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Lấy thông báo chưa đọc
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Đếm số thông báo chưa đọc
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // Tìm theo ID và user ID
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
    
    // Đánh dấu tất cả là đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
    
    // Lấy theo loại thông báo
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);
    
    // Xóa thông báo cũ (giữ lại N thông báo mới nhất)
    @Modifying
    @Query(value = "DELETE FROM notifications WHERE user_id = :userId AND id NOT IN " +
            "(SELECT id FROM (SELECT id FROM notifications WHERE user_id = :userId ORDER BY created_at DESC LIMIT :keep) AS t)", 
            nativeQuery = true)
    int deleteOldNotifications(@Param("userId") Long userId, @Param("keep") int keep);
}
