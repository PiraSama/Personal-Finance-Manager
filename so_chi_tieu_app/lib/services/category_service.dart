// lib/services/category_service.dart

import '../core/constants/api_constants.dart';
import '../models/category.dart';
import 'api_service.dart';

class CategoryService {
  final ApiService _api = ApiService();
  
  /// Lấy tất cả categories
  Future<List<Category>> getAll() async {
    try {
      final response = await _api.get(ApiConstants.categories);
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Category.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get categories error: $e');
      rethrow;
    }
  }
  
  /// Lấy categories theo loại (INCOME/EXPENSE)
  Future<List<Category>> getByType(TransactionType type) async {
    try {
      final response = await _api.get(
        ApiConstants.categories,
        queryParams: {
          'type': type == TransactionType.INCOME ? 'INCOME' : 'EXPENSE',
        },
      );
      
      if (response.data['success'] == true) {
        final List data = response.data['data'];
        return data.map((e) => Category.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      print('Get categories by type error: $e');
      rethrow;
    }
  }
  
  /// Tạo category mới
  Future<Category?> create(Category category) async {
    try {
      final response = await _api.post(
        ApiConstants.categories,
        data: category.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Category.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create category error: $e');
      rethrow;
    }
  }
  
  /// Cập nhật category
  Future<Category?> update(int id, Category category) async {
    try {
      final response = await _api.put(
        '${ApiConstants.categories}/$id',
        data: category.toJson(),
      );
      
      if (response.data['success'] == true) {
        return Category.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Update category error: $e');
      rethrow;
    }
  }
  
  /// Xóa category
  Future<bool> delete(int id) async {
    try {
      final response = await _api.delete('${ApiConstants.categories}/$id');
      return response.data['success'] == true;
    } catch (e) {
      print('Delete category error: $e');
      rethrow;
    }
  }
}
