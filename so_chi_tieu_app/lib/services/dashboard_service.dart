// lib/services/dashboard_service.dart

import '../core/constants/api_constants.dart';
import '../models/dashboard.dart';
import 'api_service.dart';

class DashboardService {
  final ApiService _api = ApiService();
  
  /// Lấy summary cho tháng/năm
  Future<DashboardSummary?> getSummary({int? month, int? year}) async {
    try {
      final now = DateTime.now();
      final response = await _api.get(
        ApiConstants.dashboardSummary,
        queryParams: {
          'month': month ?? now.month,
          'year': year ?? now.year,
        },
      );
      
      if (response.data['success'] == true) {
        return DashboardSummary.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Get dashboard summary error: $e');
      rethrow;
    }
  }
  
  /// Lấy chi tiêu theo category (cho pie chart)
  Future<List<CategoryExpense>> getExpenseByCategory({int? month, int? year}) async {
    try {
      final now = DateTime.now();
      final response = await _api.get(
        ApiConstants.expenseByCategory,
        queryParams: {
          'month': month ?? now.month,
          'year': year ?? now.year,
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => CategoryExpense.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get expense by category error: $e');
      rethrow;
    }
  }
  
  /// Lấy monthly trend (cho bar chart)
  Future<List<MonthlyTrend>> getMonthlyTrend({int months = 6}) async {
    try {
      final response = await _api.get(
        ApiConstants.monthlyTrend,
        queryParams: {'months': months},
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => MonthlyTrend.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get monthly trend error: $e');
      rethrow;
    }
  }
}
