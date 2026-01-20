// lib/services/statistics_service.dart

import '../core/constants/api_constants.dart';
import '../models/dashboard.dart';
import 'api_service.dart';

class StatisticsService {
  final ApiService _api = ApiService();
  
  /// Lấy chi tiêu theo category trong khoảng thời gian
  Future<List<CategoryExpense>> getByCategory({
    required DateTime startDate,
    required DateTime endDate,
  }) async {
    try {
      final response = await _api.get(
        ApiConstants.statisticsByCategory,
        queryParams: {
          'startDate': startDate.toIso8601String().split('T')[0],
          'endDate': endDate.toIso8601String().split('T')[0],
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => CategoryExpense.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get by category error: $e');
      rethrow;
    }
  }
  
  /// Lấy thống kê theo ngày trong tháng
  Future<List<DailyStats>> getDailyStats({int? month, int? year}) async {
    try {
      final now = DateTime.now();
      final response = await _api.get(
        ApiConstants.statisticsDaily,
        queryParams: {
          'month': month ?? now.month,
          'year': year ?? now.year,
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => DailyStats.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get daily stats error: $e');
      rethrow;
    }
  }
}

// Model cho thống kê theo ngày
class DailyStats {
  final int day;
  final double income;
  final double expense;
  final int transactionCount;

  DailyStats({
    required this.day,
    required this.income,
    required this.expense,
    required this.transactionCount,
  });

  factory DailyStats.fromJson(Map<String, dynamic> json) {
    return DailyStats(
      day: json['day'],
      income: (json['income'] as num?)?.toDouble() ?? 0,
      expense: (json['expense'] as num?)?.toDouble() ?? 0,
      transactionCount: json['transactionCount'] ?? 0,
    );
  }
}
