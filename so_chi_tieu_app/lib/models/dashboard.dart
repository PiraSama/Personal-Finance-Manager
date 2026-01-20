// lib/models/dashboard.dart

class DashboardSummary {
  final double totalIncome;
  final double totalExpense;
  final double balance;
  final double savingsRate;
  final int transactionCount;
  final List<CategoryExpense> topExpenseCategories;

  DashboardSummary({
    required this.totalIncome,
    required this.totalExpense,
    required this.balance,
    required this.savingsRate,
    required this.transactionCount,
    required this.topExpenseCategories,
  });

  factory DashboardSummary.fromJson(Map<String, dynamic> json) {
    return DashboardSummary(
      totalIncome: (json['totalIncome'] as num?)?.toDouble() ?? 0,
      totalExpense: (json['totalExpense'] as num?)?.toDouble() ?? 0,
      balance: (json['balance'] as num?)?.toDouble() ?? 0,
      savingsRate: (json['savingsRate'] as num?)?.toDouble() ?? 0,
      transactionCount: json['transactionCount'] ?? 0,
      topExpenseCategories: (json['topExpenseCategories'] as List?)
          ?.map((e) => CategoryExpense.fromJson(e))
          .toList() ?? [],
    );
  }
}

class CategoryExpense {
  final int categoryId;
  final String categoryName;
  final String? categoryIcon;
  final String? categoryColor;
  final double amount;
  final double percent;

  CategoryExpense({
    required this.categoryId,
    required this.categoryName,
    this.categoryIcon,
    this.categoryColor,
    required this.amount,
    required this.percent,
  });

  factory CategoryExpense.fromJson(Map<String, dynamic> json) {
    return CategoryExpense(
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      categoryIcon: json['categoryIcon'],
      categoryColor: json['categoryColor'],
      amount: (json['amount'] as num?)?.toDouble() ?? 0,
      percent: (json['percent'] as num?)?.toDouble() ?? 0,
    );
  }
}

class MonthlyTrend {
  final String month;
  final int monthValue;
  final int year;
  final double income;
  final double expense;
  final double balance;

  MonthlyTrend({
    required this.month,
    required this.monthValue,
    required this.year,
    required this.income,
    required this.expense,
    required this.balance,
  });

  factory MonthlyTrend.fromJson(Map<String, dynamic> json) {
    return MonthlyTrend(
      month: json['month'],
      monthValue: json['monthValue'],
      year: json['year'],
      income: (json['income'] as num?)?.toDouble() ?? 0,
      expense: (json['expense'] as num?)?.toDouble() ?? 0,
      balance: (json['balance'] as num?)?.toDouble() ?? 0,
    );
  }
}
