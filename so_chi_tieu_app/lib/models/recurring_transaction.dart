// lib/models/recurring_transaction.dart

import 'category.dart';

enum RecurringFrequency { DAILY, WEEKLY, MONTHLY, YEARLY }

class RecurringTransaction {
  final int id;
  final double amount;
  final TransactionType type;
  final String? description;
  final int categoryId;
  final String? categoryName;
  final String? categoryIcon;
  final RecurringFrequency frequency;
  final DateTime startDate;
  final DateTime? endDate;
  final DateTime? nextExecutionDate;
  final bool isActive;
  final DateTime? createdAt;

  RecurringTransaction({
    required this.id,
    required this.amount,
    required this.type,
    this.description,
    required this.categoryId,
    this.categoryName,
    this.categoryIcon,
    required this.frequency,
    required this.startDate,
    this.endDate,
    this.nextExecutionDate,
    this.isActive = true,
    this.createdAt,
  });

  factory RecurringTransaction.fromJson(Map<String, dynamic> json) {
    return RecurringTransaction(
      id: json['id'],
      amount: (json['amount'] as num).toDouble(),
      type: json['type'] == 'INCOME' 
          ? TransactionType.INCOME 
          : TransactionType.EXPENSE,
      description: json['description'],
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      categoryIcon: json['categoryIcon'],
      frequency: _parseFrequency(json['frequency']),
      startDate: DateTime.parse(json['startDate']),
      endDate: json['endDate'] != null ? DateTime.parse(json['endDate']) : null,
      nextExecutionDate: json['nextExecutionDate'] != null 
          ? DateTime.parse(json['nextExecutionDate']) 
          : null,
      isActive: json['isActive'] ?? true,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'amount': amount,
      'type': type == TransactionType.INCOME ? 'INCOME' : 'EXPENSE',
      'description': description,
      'categoryId': categoryId,
      'frequency': frequency.name,
      'startDate': startDate.toIso8601String().split('T')[0],
      'endDate': endDate?.toIso8601String().split('T')[0],
      'isActive': isActive,
    };
  }
  
  static RecurringFrequency _parseFrequency(String? value) {
    switch (value) {
      case 'DAILY':
        return RecurringFrequency.DAILY;
      case 'WEEKLY':
        return RecurringFrequency.WEEKLY;
      case 'MONTHLY':
        return RecurringFrequency.MONTHLY;
      case 'YEARLY':
        return RecurringFrequency.YEARLY;
      default:
        return RecurringFrequency.MONTHLY;
    }
  }
  
  String get frequencyDisplay {
    switch (frequency) {
      case RecurringFrequency.DAILY:
        return 'Hàng ngày';
      case RecurringFrequency.WEEKLY:
        return 'Hàng tuần';
      case RecurringFrequency.MONTHLY:
        return 'Hàng tháng';
      case RecurringFrequency.YEARLY:
        return 'Hàng năm';
    }
  }
}
