// lib/providers/transaction_provider.dart

import 'package:flutter/material.dart';
import '../models/transaction.dart';
import '../models/category.dart';
import '../services/transaction_service.dart';
import '../services/category_service.dart';

class TransactionProvider with ChangeNotifier {
  final TransactionService _transactionService = TransactionService();
  final CategoryService _categoryService = CategoryService();
  
  List<Transaction> _transactions = [];
  List<Category> _categories = [];
  bool _isLoading = false;
  String? _error;
  
  List<Transaction> get transactions => _transactions;
  List<Category> get categories => _categories;
  List<Category> get incomeCategories => 
      _categories.where((c) => c.type == TransactionType.INCOME).toList();
  List<Category> get expenseCategories => 
      _categories.where((c) => c.type == TransactionType.EXPENSE).toList();
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  // Tính tổng thu nhập
  double get totalIncome => _transactions
      .where((t) => t.type == TransactionType.INCOME)
      .fold(0, (sum, t) => sum + t.amount);
  
  // Tính tổng chi tiêu
  double get totalExpense => _transactions
      .where((t) => t.type == TransactionType.EXPENSE)
      .fold(0, (sum, t) => sum + t.amount);
  
  // Tính số dư
  double get balance => totalIncome - totalExpense;
  
  /// Load tất cả transactions
  Future<void> loadTransactions() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _transactions = await _transactionService.getAll();
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải danh sách giao dịch';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Load tất cả categories
  Future<void> loadCategories() async {
    try {
      _categories = await _categoryService.getAll();
      notifyListeners();
    } catch (e) {
      print('Load categories error: $e');
    }
  }
  
  /// Thêm giao dịch mới
  Future<bool> addTransaction(Transaction transaction) async {
    try {
      final newTransaction = await _transactionService.create(transaction);
      if (newTransaction != null) {
        _transactions.insert(0, newTransaction);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể tạo giao dịch';
      notifyListeners();
      return false;
    }
  }
  
  /// Cập nhật giao dịch
  Future<bool> updateTransaction(int id, Transaction transaction) async {
    try {
      final updated = await _transactionService.update(id, transaction);
      if (updated != null) {
        final index = _transactions.indexWhere((t) => t.id == id);
        if (index != -1) {
          _transactions[index] = updated;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể cập nhật giao dịch';
      notifyListeners();
      return false;
    }
  }
  
  /// Xóa giao dịch
  Future<bool> deleteTransaction(int id) async {
    try {
      final success = await _transactionService.delete(id);
      if (success) {
        _transactions.removeWhere((t) => t.id == id);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể xóa giao dịch';
      notifyListeners();
      return false;
    }
  }
  
  void clearError() {
    _error = null;
    notifyListeners();
  }
}
