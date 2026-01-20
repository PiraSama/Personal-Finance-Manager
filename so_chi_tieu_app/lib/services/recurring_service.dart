// lib/services/recurring_service.dart

import '../core/constants/api_constants.dart';
import '../models/recurring_transaction.dart';
import 'api_service.dart';

class RecurringService {
  final ApiService _api = ApiService();
  
  /// Lấy tất cả recurring transactions
  Future<List<RecurringTransaction>> getAll() async {
    try {
      final response = await _api.get(ApiConstants.recurringTransactions);
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => RecurringTransaction.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get recurring transactions error: $e');
      rethrow;
    }
  }
  
  /// Lấy chi tiết một recurring
  Future<RecurringTransaction?> getById(int id) async {
    try {
      final response = await _api.get('${ApiConstants.recurringTransactions}/$id');
      
      if (response.data['success'] == true) {
        return RecurringTransaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Get recurring by id error: $e');
      rethrow;
    }
  }
  
  /// Tạo recurring transaction mới
  Future<RecurringTransaction?> create(RecurringTransaction recurring) async {
    try {
      final response = await _api.post(
        ApiConstants.recurringTransactions,
        data: recurring.toJson(),
      );
      
      if (response.data['success'] == true) {
        return RecurringTransaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create recurring error: $e');
      rethrow;
    }
  }
  
  /// Cập nhật recurring transaction
  Future<RecurringTransaction?> update(int id, RecurringTransaction recurring) async {
    try {
      final response = await _api.put(
        '${ApiConstants.recurringTransactions}/$id',
        data: recurring.toJson(),
      );
      
      if (response.data['success'] == true) {
        return RecurringTransaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Update recurring error: $e');
      rethrow;
    }
  }
  
  /// Xóa recurring transaction
  Future<bool> delete(int id) async {
    try {
      final response = await _api.delete('${ApiConstants.recurringTransactions}/$id');
      return response.data['success'] == true;
    } catch (e) {
      print('Delete recurring error: $e');
      rethrow;
    }
  }
  
  /// Toggle active/inactive
  Future<RecurringTransaction?> toggleActive(int id) async {
    try {
      final response = await _api.patch('${ApiConstants.recurringTransactions}/$id/toggle');
      
      if (response.data['success'] == true) {
        return RecurringTransaction.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Toggle recurring error: $e');
      rethrow;
    }
  }
}
