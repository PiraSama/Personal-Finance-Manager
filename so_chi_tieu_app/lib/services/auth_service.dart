// lib/services/auth_service.dart

import '../core/constants/api_constants.dart';
import '../models/user.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _api = ApiService();
  
  User? _currentUser;
  User? get currentUser => _currentUser;
  
  Future<User?> login(String email, String password) async {
    try {
      final response = await _api.post(
        ApiConstants.login,
        data: {
          'email': email,
          'password': password,
        },
      );
      
      if (response.data['success'] == true) {
        final data = response.data['data'];
        await _api.saveToken(data['token']);
        _currentUser = User.fromJson(data['user']);
        return _currentUser;
      }
      return null;
    } catch (e) {
      print('Login error: $e');
      rethrow;
    }
  }
  
  Future<User?> register(String email, String password, String fullName) async {
    try {
      final response = await _api.post(
        ApiConstants.register,
        data: {
          'email': email,
          'password': password,
          'fullName': fullName,
        },
      );
      
      if (response.data['success'] == true) {
        final data = response.data['data'];
        await _api.saveToken(data['token']);
        _currentUser = User.fromJson(data['user']);
        return _currentUser;
      }
      return null;
    } catch (e) {
      print('Register error: $e');
      rethrow;
    }
  }
  
  Future<void> logout() async {
    await _api.removeToken();
    _currentUser = null;
  }
  
  Future<bool> checkAuth() async {
    await _api.loadToken();
    return _api.isLoggedIn;
  }
}
