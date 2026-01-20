// lib/models/budget.dart

class Budget {
  final int id;
  final int categoryId;
  final String? categoryName;
  final String? categoryIcon;
  final double amount;
  final int month;
  final int year;
  final double spent;
  final double remaining;
  final double percentUsed;

  Budget({
    required this.id,
    required this.categoryId,
    this.categoryName,
    this.categoryIcon,
    required this.amount,
    required this.month,
    required this.year,
    this.spent = 0,
    this.remaining = 0,
    this.percentUsed = 0,
  });

  factory Budget.fromJson(Map<String, dynamic> json) {
    return Budget(
      id: json['id'],
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      categoryIcon: json['categoryIcon'],
      amount: (json['amount'] as num).toDouble(),
      month: json['month'],
      year: json['year'],
      spent: (json['spent'] as num?)?.toDouble() ?? 0,
      remaining: (json['remaining'] as num?)?.toDouble() ?? 0,
      percentUsed: (json['percentUsed'] as num?)?.toDouble() ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'categoryId': categoryId,
      'amount': amount,
      'month': month,
      'year': year,
    };
  }
  
  // Helper để check trạng thái
  bool get isOverBudget => spent > amount;
  bool get isWarning => percentUsed >= 80 && percentUsed < 100;
  bool get isNormal => percentUsed < 80;
}
