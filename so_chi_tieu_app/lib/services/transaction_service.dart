// lib/services/transaction_service.dart

import '../core/constants/api_constants.dart';
import '../models/transaction.dart';
import '../models/category.dart';
import 'api_service.dart';

class TransactionService {
  final ApiService _api = ApiService();
  
  /// Lấy tất cả giao dịch
  Future<List<Transaction>> getAll() async {
    try {
      final response = await _api.get(ApiConstants.transactions);
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Transaction.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get transactions error: $e');
      rethrow;
    }
  }
  
  /// Lấy giao dịch theo khoảng thời gian
  Future<List<Transaction>> getByDateRange(DateTime startDate, DateTime endDate) async {
    try {
      final response = await _api.get(
        ApiConstants.transactions,
        queryParams: {
          'startDate': startDate.toIso8601String().split('T')[0],
          'endDate': endDate.toIso8601String().split('T')[0],
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Transaction.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get transactions by date error: $e');
      rethrow;
    }
  }
  
  /// Lấy giao dịch theo loại (INCOME/EXPENSE)
  Future<List<Transaction>> getByType(TransactionType type) async {
    try {
      final response = await _api.get(
        ApiConstants.transactions,
        queryParams: {
          'type': type == TransactionType.INCOME ? 'INCOME' : 'EXPENSE',
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Transaction.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get transactions by type error: $e');
      rethrow;
    }
  }
  
  /// Tạo giao dịch mới
  Future<Transaction?> create(Transaction transaction) async {
    try {
      final response = await _api.post(
        ApiConstants.transactions,
        data: transaction.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Transaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create transaction error: $e');
      rethrow;
    }
  }
  
  /// Cập nhật giao dịch
  Future<Transaction?> update(int id, Transaction transaction) async {
    try {
      final response = await _api.put(
        '${ApiConstants.transactions}/$id',
        data: transaction.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Transaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Update transaction error: $e');
      rethrow;
    }
  }
  
  /// Xóa giao dịch
  Future<bool> delete(int id) async {
    try {
      final response = await _api.delete('${ApiConstants.transactions}/$id');
      return response.data['success'] == true;
    } catch (e) {
      print('Delete transaction error: $e');
      rethrow;
    }
  }
}
