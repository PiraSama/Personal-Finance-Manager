// lib/models/transaction.dart

import 'category.dart';

class Transaction {
  final int id;
  final double amount;
  final TransactionType type;
  final String? description;
  final DateTime transactionDate;
  final int categoryId;
  final String? categoryName;
  final String? categoryIcon;
  final String? categoryColor;

  Transaction({
    required this.id,
    required this.amount,
    required this.type,
    this.description,
    required this.transactionDate,
    required this.categoryId,
    this.categoryName,
    this.categoryIcon,
    this.categoryColor,
  });

  factory Transaction.fromJson(Map<String, dynamic> json) {
    return Transaction(
      id: json['id'],
      amount: (json['amount'] as num).toDouble(),
      type: json['type'] == 'INCOME' 
          ? TransactionType.INCOME 
          : TransactionType.EXPENSE,
      description: json['description'],
      transactionDate: DateTime.parse(json['transactionDate']),
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      categoryIcon: json['categoryIcon'],
      categoryColor: json['categoryColor'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'amount': amount,
      'type': type == TransactionType.INCOME ? 'INCOME' : 'EXPENSE',
      'description': description,
      'transactionDate': transactionDate.toIso8601String().split('T')[0],
      'categoryId': categoryId,
    };
  }
}
