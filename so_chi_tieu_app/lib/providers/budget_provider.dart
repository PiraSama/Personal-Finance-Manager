// lib/providers/budget_provider.dart

import 'package:flutter/material.dart';
import '../models/budget.dart';
import '../models/category.dart';
import '../services/budget_service.dart';
import '../services/category_service.dart';

class BudgetProvider with ChangeNotifier {
  final BudgetService _budgetService = BudgetService();
  final CategoryService _categoryService = CategoryService();
  
  List<Budget> _budgets = [];
  List<Category> _expenseCategories = [];
  bool _isLoading = false;
  String? _error;
  
  int _selectedMonth = DateTime.now().month;
  int _selectedYear = DateTime.now().year;
  
  List<Budget> get budgets => _budgets;
  List<Category> get expenseCategories => _expenseCategories;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get selectedMonth => _selectedMonth;
  int get selectedYear => _selectedYear;
  
  // Tính tổng ngân sách
  double get totalBudget => _budgets.fold(0, (sum, b) => sum + b.amount);
  
  // Tính tổng đã chi
  double get totalSpent => _budgets.fold(0, (sum, b) => sum + b.spent);
  
  // Categories chưa có budget
  List<Category> get categoriesWithoutBudget {
    final budgetCategoryIds = _budgets.map((b) => b.categoryId).toSet();
    return _expenseCategories
        .where((c) => !budgetCategoryIds.contains(c.id))
        .toList();
  }
  
  /// Load budgets cho tháng hiện tại
  Future<void> loadBudgets() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final results = await Future.wait([
        _budgetService.getByMonth(_selectedMonth, _selectedYear),
        _categoryService.getByType(TransactionType.EXPENSE),
      ]);
      
      _budgets = results[0] as List<Budget>;
      _expenseCategories = results[1] as List<Category>;
      
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải danh sách ngân sách';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Thêm budget mới
  Future<bool> addBudget(Budget budget) async {
    try {
      final newBudget = await _budgetService.create(budget);
      if (newBudget != null) {
        _budgets.add(newBudget);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể tạo ngân sách';
      notifyListeners();
      return false;
    }
  }
  
  /// Cập nhật budget
  Future<bool> updateBudget(int id, Budget budget) async {
    try {
      final updated = await _budgetService.update(id, budget);
      if (updated != null) {
        final index = _budgets.indexWhere((b) => b.id == id);
        if (index != -1) {
          _budgets[index] = updated;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể cập nhật ngân sách';
      notifyListeners();
      return false;
    }
  }
  
  /// Xóa budget
  Future<bool> deleteBudget(int id) async {
    try {
      final success = await _budgetService.delete(id);
      if (success) {
        _budgets.removeWhere((b) => b.id == id);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể xóa ngân sách';
      notifyListeners();
      return false;
    }
  }
  
  /// Đổi tháng
  void changeMonth(int month, int year) {
    _selectedMonth = month;
    _selectedYear = year;
    loadBudgets();
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadBudgets();
  }
}
