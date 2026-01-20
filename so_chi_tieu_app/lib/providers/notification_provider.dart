// lib/providers/notification_provider.dart

import 'package:flutter/material.dart';
import '../models/notification.dart';
import '../services/notification_service.dart';

class NotificationProvider with ChangeNotifier {
  final NotificationService _service = NotificationService();
  
  List<AppNotification> _notifications = [];
  int _unreadCount = 0;
  bool _isLoading = false;
  String? _error;
  
  List<AppNotification> get notifications => _notifications;
  int get unreadCount => _unreadCount;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get hasUnread => _unreadCount > 0;
  
  /// Load tất cả notifications
  Future<void> loadNotifications() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _notifications = await _service.getAll();
      _unreadCount = _notifications.where((n) => !n.isRead).length;
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải thông báo';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Load số thông báo chưa đọc
  Future<void> loadUnreadCount() async {
    try {
      _unreadCount = await _service.countUnread();
      notifyListeners();
    } catch (e) {
      // Ignore error
    }
  }
  
  /// Đánh dấu đã đọc 1 thông báo
  Future<bool> markAsRead(int id) async {
    try {
      final success = await _service.markAsRead(id);
      if (success) {
        final index = _notifications.indexWhere((n) => n.id == id);
        if (index != -1) {
          final old = _notifications[index];
          _notifications[index] = AppNotification(
            id: old.id,
            type: old.type,
            typeDisplay: old.typeDisplay,
            title: old.title,
            message: old.message,
            isRead: true,
            extraData: old.extraData,
            createdAt: old.createdAt,
            timeAgo: old.timeAgo,
          );
          _unreadCount = _notifications.where((n) => !n.isRead).length;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
  
  /// Đánh dấu tất cả đã đọc
  Future<bool> markAllAsRead() async {
    try {
      final success = await _service.markAllAsRead();
      if (success) {
        _notifications = _notifications.map((n) => AppNotification(
          id: n.id,
          type: n.type,
          typeDisplay: n.typeDisplay,
          title: n.title,
          message: n.message,
          isRead: true,
          extraData: n.extraData,
          createdAt: n.createdAt,
          timeAgo: n.timeAgo,
        )).toList();
        _unreadCount = 0;
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
  
  /// Xóa thông báo
  Future<bool> deleteNotification(int id) async {
    try {
      final success = await _service.delete(id);
      if (success) {
        _notifications.removeWhere((n) => n.id == id);
        _unreadCount = _notifications.where((n) => !n.isRead).length;
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadNotifications();
  }
}
