// lib/models/notification.dart

enum NotificationType {
  WELCOME,
  BUDGET_WARNING,
  BUDGET_EXCEEDED,
  RECURRING_DUE,
  RECURRING_PROCESSED,
  REMINDER,
  INFO,
}

class AppNotification {
  final int id;
  final NotificationType type;
  final String typeDisplay;
  final String title;
  final String? message;
  final bool isRead;
  final String? extraData;
  final DateTime? createdAt;
  final String? timeAgo;

  AppNotification({
    required this.id,
    required this.type,
    required this.typeDisplay,
    required this.title,
    this.message,
    required this.isRead,
    this.extraData,
    this.createdAt,
    this.timeAgo,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      id: json['id'],
      type: _parseType(json['type']),
      typeDisplay: json['typeDisplay'] ?? '',
      title: json['title'],
      message: json['message'],
      isRead: json['isRead'] ?? false,
      extraData: json['extraData'],
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt']) 
          : null,
      timeAgo: json['timeAgo'],
    );
  }

  static NotificationType _parseType(String? type) {
    switch (type) {
      case 'WELCOME': return NotificationType.WELCOME;
      case 'BUDGET_WARNING': return NotificationType.BUDGET_WARNING;
      case 'BUDGET_EXCEEDED': return NotificationType.BUDGET_EXCEEDED;
      case 'RECURRING_DUE': return NotificationType.RECURRING_DUE;
      case 'RECURRING_PROCESSED': return NotificationType.RECURRING_PROCESSED;
      case 'REMINDER': return NotificationType.REMINDER;
      default: return NotificationType.INFO;
    }
  }
}
