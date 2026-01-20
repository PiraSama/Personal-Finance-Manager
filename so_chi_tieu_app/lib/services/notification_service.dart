// lib/services/notification_service.dart

import '../core/constants/api_constants.dart';
import '../models/notification.dart';
import 'api_service.dart';

class NotificationService {
  final ApiService _api = ApiService();
  
  /// Lấy tất cả notifications
  Future<List<AppNotification>> getAll({bool unreadOnly = false}) async {
    try {
      final response = await _api.get(
        ApiConstants.notifications,
        queryParams: unreadOnly ? {'unreadOnly': true} : null,
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => AppNotification.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get notifications error: $e');
      rethrow;
    }
  }
  
  /// Đếm số thông báo chưa đọc
  Future<int> countUnread() async {
    try {
      final response = await _api.get(ApiConstants.notificationsCount);
      
      if (response.data['success'] == true) {
        return response.data['data'] ?? 0;
      }
      return 0;
    } catch (e) {
      print('Count unread error: $e');
      return 0;
    }
  }
  
  /// Đánh dấu đã đọc 1 thông báo
  Future<bool> markAsRead(int id) async {
    try {
      final response = await _api.patch('${ApiConstants.notifications}/$id/read');
      return response.data['success'] == true;
    } catch (e) {
      print('Mark as read error: $e');
      return false;
    }
  }
  
  /// Đánh dấu tất cả đã đọc
  Future<bool> markAllAsRead() async {
    try {
      final response = await _api.patch('${ApiConstants.notifications}/read-all');
      return response.data['success'] == true;
    } catch (e) {
      print('Mark all as read error: $e');
      return false;
    }
  }
  
  /// Xóa thông báo
  Future<bool> delete(int id) async {
    try {
      final response = await _api.delete('${ApiConstants.notifications}/$id');
      return response.data['success'] == true;
    } catch (e) {
      print('Delete notification error: $e');
      return false;
    }
  }
}
