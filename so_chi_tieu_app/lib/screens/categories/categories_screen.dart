// lib/screens/categories/categories_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_colors.dart';
import '../../models/category.dart';
import '../../providers/category_provider.dart';
import 'add_category_screen.dart';

class CategoriesScreen extends StatefulWidget {
  const CategoriesScreen({super.key});

  @override
  State<CategoriesScreen> createState() => _CategoriesScreenState();
}

class _CategoriesScreenState extends State<CategoriesScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<CategoryProvider>(context, listen: false).loadCategories();
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Danh mục'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
        bottom: TabBar(
          controller: _tabController,
          labelColor: AppColors.primary,
          unselectedLabelColor: AppColors.textSecondary,
          indicatorColor: AppColors.primary,
          tabs: const [
            Tab(text: 'Chi tiêu'),
            Tab(text: 'Thu nhập'),
          ],
        ),
      ),
      body: Consumer<CategoryProvider>(
        builder: (context, provider, _) {
          if (provider.isLoading && provider.categories.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }
          
          return TabBarView(
            controller: _tabController,
            children: [
              _buildCategoryList(provider.expenseCategories, provider, TransactionType.EXPENSE),
              _buildCategoryList(provider.incomeCategories, provider, TransactionType.INCOME),
            ],
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _navigateToAddCategory(),
        backgroundColor: AppColors.primary,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
  
  Widget _buildCategoryList(
    List<Category> categories, 
    CategoryProvider provider,
    TransactionType type,
  ) {
    if (categories.isEmpty) {
      return _buildEmptyState(type);
    }
    
    return RefreshIndicator(
      onRefresh: provider.refresh,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: categories.length,
        itemBuilder: (context, index) {
          return _buildCategoryItem(categories[index], provider);
        },
      ),
    );
  }
  
  Widget _buildEmptyState(TransactionType type) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.category, size: 80, color: Colors.grey[300]),
          const SizedBox(height: 16),
          Text(
            type == TransactionType.EXPENSE
                ? 'Chưa có danh mục chi tiêu'
                : 'Chưa có danh mục thu nhập',
            style: const TextStyle(color: AppColors.textSecondary),
          ),
          const SizedBox(height: 8),
          const Text(
            'Nhấn + để thêm danh mục mới',
            style: TextStyle(color: AppColors.textHint),
          ),
        ],
      ),
    );
  }
  
  Widget _buildCategoryItem(Category category, CategoryProvider provider) {
    final isExpense = category.type == TransactionType.EXPENSE;
    final color = isExpense ? AppColors.expense : AppColors.income;
    
    return Dismissible(
      key: Key(category.id.toString()),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 20),
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: AppColors.error,
          borderRadius: BorderRadius.circular(16),
        ),
        child: const Icon(Icons.delete, color: Colors.white),
      ),
      confirmDismiss: (direction) => _confirmDelete(category, provider),
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        child: ListTile(
          onTap: () => _navigateToEditCategory(category),
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          leading: Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: color.withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Center(
              child: Text(
                category.icon ?? '📁',
                style: const TextStyle(fontSize: 24),
              ),
            ),
          ),
          title: Text(
            category.name,
            style: const TextStyle(
              fontWeight: FontWeight.w600,
              fontSize: 15,
            ),
          ),
          subtitle: Text(
            isExpense ? 'Chi tiêu' : 'Thu nhập',
            style: TextStyle(
              color: color,
              fontSize: 12,
            ),
          ),
          trailing: const Icon(Icons.chevron_right, color: AppColors.textHint),
        ),
      ),
    );
  }
  
  Future<bool> _confirmDelete(Category category, CategoryProvider provider) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xóa danh mục'),
        content: Text('Bạn có chắc muốn xóa danh mục "${category.name}"?\n\n'
            'Lưu ý: Các giao dịch sử dụng danh mục này vẫn được giữ.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Hủy'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Xóa', style: TextStyle(color: AppColors.error)),
          ),
        ],
      ),
    );
    
    if (result == true) {
      final success = await provider.deleteCategory(category.id);
      if (success && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Đã xóa danh mục')),
        );
      }
      return success;
    }
    
    return false;
  }
  
  void _navigateToAddCategory() {
    // Determine which tab is active
    final type = _tabController.index == 0 
        ? TransactionType.EXPENSE 
        : TransactionType.INCOME;
    
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => AddCategoryScreen(initialType: type),
      ),
    ).then((_) {
      Provider.of<CategoryProvider>(context, listen: false).loadCategories();
    });
  }
  
  void _navigateToEditCategory(Category category) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => AddCategoryScreen(category: category),
      ),
    ).then((_) {
      Provider.of<CategoryProvider>(context, listen: false).loadCategories();
    });
  }
}
