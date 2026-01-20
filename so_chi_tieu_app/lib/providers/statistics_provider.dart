// lib/providers/statistics_provider.dart

import 'package:flutter/material.dart';
import '../models/dashboard.dart';
import '../services/statistics_service.dart';
import '../services/dashboard_service.dart';

class StatisticsProvider with ChangeNotifier {
  final StatisticsService _statisticsService = StatisticsService();
  final DashboardService _dashboardService = DashboardService();
  
  List<CategoryExpense> _expenseByCategory = [];
  List<MonthlyTrend> _monthlyTrend = [];
  List<DailyStats> _dailyStats = [];
  bool _isLoading = false;
  String? _error;
  
  int _selectedMonth = DateTime.now().month;
  int _selectedYear = DateTime.now().year;
  
  List<CategoryExpense> get expenseByCategory => _expenseByCategory;
  List<MonthlyTrend> get monthlyTrend => _monthlyTrend;
  List<DailyStats> get dailyStats => _dailyStats;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get selectedMonth => _selectedMonth;
  int get selectedYear => _selectedYear;
  
  // Tính tổng chi tiêu
  double get totalExpense => _expenseByCategory.fold(0, (sum, c) => sum + c.amount);
  
  /// Load statistics data
  Future<void> loadStatistics() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      // Get date range for selected month
      final startDate = DateTime(_selectedYear, _selectedMonth, 1);
      final endDate = DateTime(_selectedYear, _selectedMonth + 1, 0);
      
      // Load parallel
      final results = await Future.wait([
        _statisticsService.getByCategory(startDate: startDate, endDate: endDate),
        _dashboardService.getMonthlyTrend(months: 6),
        _statisticsService.getDailyStats(month: _selectedMonth, year: _selectedYear),
      ]);
      
      _expenseByCategory = results[0] as List<CategoryExpense>;
      _monthlyTrend = results[1] as List<MonthlyTrend>;
      _dailyStats = results[2] as List<DailyStats>;
      
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải dữ liệu thống kê';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Đổi tháng
  void changeMonth(int month, int year) {
    _selectedMonth = month;
    _selectedYear = year;
    loadStatistics();
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadStatistics();
  }
}
