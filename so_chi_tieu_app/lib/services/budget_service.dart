// lib/services/budget_service.dart

import '../core/constants/api_constants.dart';
import '../models/budget.dart';
import 'api_service.dart';

class BudgetService {
  final ApiService _api = ApiService();
  
  /// Lấy tất cả budgets
  Future<List<Budget>> getAll() async {
    try {
      final response = await _api.get(ApiConstants.budgets);
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Budget.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get budgets error: $e');
      rethrow;
    }
  }
  
  /// Lấy budgets theo tháng/năm
  Future<List<Budget>> getByMonth(int month, int year) async {
    try {
      final response = await _api.get(
        ApiConstants.budgets,
        queryParams: {
          'month': month,
          'year': year,
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Budget.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get budgets by month error: $e');
      rethrow;
    }
  }
  
  /// Tạo budget mới
  Future<Budget?> create(Budget budget) async {
    try {
      final response = await _api.post(
        ApiConstants.budgets,
        data: budget.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Budget.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create budget error: $e');
      rethrow;
    }
  }
  
  /// Cập nhật budget
  Future<Budget?> update(int id, Budget budget) async {
    try {
      final response = await _api.put(
        '${ApiConstants.budgets}/$id',
        data: budget.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Budget.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Update budget error: $e');
      rethrow;
    }
  }
  
  /// Xóa budget
  Future<bool> delete(int id) async {
    try {
      final response = await _api.delete('${ApiConstants.budgets}/$id');
      return response.data['success'] == true;
    } catch (e) {
      print('Delete budget error: $e');
      rethrow;
    }
  }
}
