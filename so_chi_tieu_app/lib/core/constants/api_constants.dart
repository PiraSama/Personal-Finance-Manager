// lib/core/constants/api_constants.dart

class ApiConstants {
  static const String baseUrl = 'http://localhost:8080/api'; // Windows Desktop / Chrome
  // static const String baseUrl = 'http://10.0.2.2:8080/api'; // Android Emulator
  
  // Auth endpoints
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  
  // Category endpoints
  static const String categories = '/categories';
  
  // Transaction endpoints
  static const String transactions = '/transactions';
  
  // Budget endpoints
  static const String budgets = '/budgets';
  
  // Dashboard endpoints
  static const String dashboardSummary = '/dashboard/summary';
  static const String expenseByCategory = '/dashboard/chart/expense-by-category';
  static const String monthlyTrend = '/dashboard/chart/monthly-trend';
  
  // Statistics endpoints
  static const String statisticsByCategory = '/statistics/by-category';
  static const String statisticsDaily = '/statistics/daily';
  static const String statisticsComparison = '/statistics/comparison';
  
  // Notifications endpoints
  static const String notifications = '/notifications';
  static const String notificationsCount = '/notifications/count';
  
  // Recurring transactions endpoints
  static const String recurringTransactions = '/recurring-transactions';
}
