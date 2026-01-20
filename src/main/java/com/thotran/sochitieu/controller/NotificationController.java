package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.NotificationResponse;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller cho Notification APIs.
 * Base path: /api/notifications
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * GET /api/notifications
     * Lấy tất cả thông báo
     * 
     * @param unreadOnly Chỉ lấy thông báo chưa đọc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll(
            @RequestParam(required = false, defaultValue = "false") Boolean unreadOnly) {
        
        List<NotificationResponse> notifications;
        
        if (unreadOnly) {
            notifications = notificationService.getUnread(getCurrentUserId());
        } else {
            notifications = notificationService.getAllByUser(getCurrentUserId());
        }
        
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    /**
     * GET /api/notifications/count
     * Đếm số thông báo chưa đọc
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countUnread() {
        
        Long count = notificationService.countUnread(getCurrentUserId());
        
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }
    
    /**
     * PATCH /api/notifications/{id}/read
     * Đánh dấu 1 thông báo là đã đọc
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        
        NotificationResponse notification = notificationService.markAsRead(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success(notification));
    }
    
    /**
     * PATCH /api/notifications/read-all
     * Đánh dấu tất cả là đã đọc
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead() {
        
        int count = notificationService.markAllAsRead(getCurrentUserId());
        
        return ResponseEntity.ok(ApiResponse.success(
                "Đã đánh dấu tất cả là đã đọc",
                Map.of("markedCount", count)));
    }
    
    /**
     * DELETE /api/notifications/{id}
     * Xóa thông báo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        
        notificationService.delete(getCurrentUserId(), id);
        
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thông báo"));
    }
}
