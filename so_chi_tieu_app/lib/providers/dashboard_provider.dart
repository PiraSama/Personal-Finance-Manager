// lib/providers/dashboard_provider.dart

import 'package:flutter/material.dart';
import '../models/dashboard.dart';
import '../models/transaction.dart';
import '../services/dashboard_service.dart';
import '../services/transaction_service.dart';

class DashboardProvider with ChangeNotifier {
  final DashboardService _dashboardService = DashboardService();
  final TransactionService _transactionService = TransactionService();
  
  DashboardSummary? _summary;
  List<CategoryExpense> _expenseByCategory = [];
  List<MonthlyTrend> _monthlyTrend = [];
  List<Transaction> _recentTransactions = [];
  bool _isLoading = false;
  String? _error;
  
  int _selectedMonth = DateTime.now().month;
  int _selectedYear = DateTime.now().year;
  
  DashboardSummary? get summary => _summary;
  List<CategoryExpense> get expenseByCategory => _expenseByCategory;
  List<MonthlyTrend> get monthlyTrend => _monthlyTrend;
  List<Transaction> get recentTransactions => _recentTransactions;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get selectedMonth => _selectedMonth;
  int get selectedYear => _selectedYear;
  
  /// Load tất cả data cho dashboard
  Future<void> loadDashboard() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      // Load parallel
      final results = await Future.wait([
        _dashboardService.getSummary(month: _selectedMonth, year: _selectedYear),
        _dashboardService.getExpenseByCategory(month: _selectedMonth, year: _selectedYear),
        _dashboardService.getMonthlyTrend(),
        _transactionService.getAll(),
      ]);
      
      _summary = results[0] as DashboardSummary?;
      _expenseByCategory = results[1] as List<CategoryExpense>;
      _monthlyTrend = results[2] as List<MonthlyTrend>;
      
      // Get recent 5 transactions
      final allTransactions = results[3] as List<Transaction>;
      _recentTransactions = allTransactions.take(5).toList();
      
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = 'Không thể tải dữ liệu dashboard';
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// Đổi tháng
  void changeMonth(int month, int year) {
    _selectedMonth = month;
    _selectedYear = year;
    loadDashboard();
  }
  
  /// Refresh
  Future<void> refresh() async {
    await loadDashboard();
  }
}
