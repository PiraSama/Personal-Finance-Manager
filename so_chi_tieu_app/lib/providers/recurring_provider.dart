// lib/providers/recurring_provider.dart

import 'package:flutter/material.dart';
import '../models/recurring_transaction.dart';
import '../models/category.dart';
import '../services/recurring_service.dart';
import '../services/category_service.dart';

class RecurringProvider with ChangeNotifier {
  final RecurringService _recurringService = RecurringService();
  final CategoryService _categoryService = CategoryService();
  
  List<RecurringTransaction> _recurringTransactions = [];
  List<Category> _categories = [];
  bool _isLoading = false;
  String? _error;
  
  List<RecurringTransaction> get recurringTransactions => _recurringTransactions;
  List<RecurringTransaction> get activeRecurrings => 
      _recurringTransactions.where((r) => r.isActive).toList();
  List<RecurringTransaction> get inactiveRecurrings => 
      _recurringTransactions.where((r) => !r.isActive).toList();
  List<Category> get categories => _categories;
  List<Category> get incomeCategories => 
      _categories.where((c) => c.type == TransactionType.INCOME).toList();
  List<Category> get expenseCategories => 
      _categories.where((c) => c.type == TransactionType.EXPENSE).toList();
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  /// Load tất cả recurring transactions
  Future<void> loadRecurringTransactions() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final results = await Future.wait([
        _recurringService.getAll(),
        _categoryService.getAll(),
      ]);
      
      _recurringTransactions = results[0] as List<RecurringTransaction>;
      _categories = results[1] as List<Category>;
      
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải giao dịch định kỳ';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Thêm recurring transaction mới
  Future<bool> addRecurring(RecurringTransaction recurring) async {
    try {
      final newRecurring = await _recurringService.create(recurring);
      if (newRecurring != null) {
        _recurringTransactions.add(newRecurring);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể tạo giao dịch định kỳ';
      notifyListeners();
      return false;
    }
  }
  
  /// Cập nhật recurring transaction
  Future<bool> updateRecurring(int id, RecurringTransaction recurring) async {
    try {
      final updated = await _recurringService.update(id, recurring);
      if (updated != null) {
        final index = _recurringTransactions.indexWhere((r) => r.id == id);
        if (index != -1) {
          _recurringTransactions[index] = updated;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể cập nhật giao dịch định kỳ';
      notifyListeners();
      return false;
    }
  }
  
  /// Xóa recurring transaction
  Future<bool> deleteRecurring(int id) async {
    try {
      final success = await _recurringService.delete(id);
      if (success) {
        _recurringTransactions.removeWhere((r) => r.id == id);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể xóa giao dịch định kỳ';
      notifyListeners();
      return false;
    }
  }
  
  /// Toggle active/inactive
  Future<bool> toggleActive(int id) async {
    try {
      final updated = await _recurringService.toggleActive(id);
      if (updated != null) {
        final index = _recurringTransactions.indexWhere((r) => r.id == id);
        if (index != -1) {
          _recurringTransactions[index] = updated;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể thay đổi trạng thái';
      notifyListeners();
      return false;
    }
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadRecurringTransactions();
  }
}
