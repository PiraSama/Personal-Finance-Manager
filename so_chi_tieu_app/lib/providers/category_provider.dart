// lib/providers/category_provider.dart

import 'package:flutter/material.dart';
import '../models/category.dart';
import '../services/category_service.dart';

class CategoryProvider with ChangeNotifier {
  final CategoryService _service = CategoryService();
  
  List<Category> _categories = [];
  bool _isLoading = false;
  String? _error;
  
  List<Category> get categories => _categories;
  List<Category> get incomeCategories => 
      _categories.where((c) => c.type == TransactionType.INCOME).toList();
  List<Category> get expenseCategories => 
      _categories.where((c) => c.type == TransactionType.EXPENSE).toList();
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  /// Load tất cả categories
  Future<void> loadCategories() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _categories = await _service.getAll();
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải danh mục';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Thêm category mới
  Future<bool> addCategory(Category category) async {
    try {
      final newCategory = await _service.create(category);
      if (newCategory != null) {
        _categories.add(newCategory);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể tạo danh mục';
      notifyListeners();
      return false;
    }
  }
  
  /// Cập nhật category
  Future<bool> updateCategory(int id, Category category) async {
    try {
      final updated = await _service.update(id, category);
      if (updated != null) {
        final index = _categories.indexWhere((c) => c.id == id);
        if (index != -1) {
          _categories[index] = updated;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể cập nhật danh mục';
      notifyListeners();
      return false;
    }
  }
  
  /// Xóa category
  Future<bool> deleteCategory(int id) async {
    try {
      final success = await _service.delete(id);
      if (success) {
        _categories.removeWhere((c) => c.id == id);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      _error = 'Không thể xóa danh mục';
      notifyListeners();
      return false;
    }
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadCategories();
  }
}
