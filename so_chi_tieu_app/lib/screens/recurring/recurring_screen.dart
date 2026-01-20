// lib/screens/recurring/recurring_screen.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_colors.dart';
import '../../models/recurring_transaction.dart';
import '../../models/category.dart';
import '../../providers/recurring_provider.dart';
import 'add_recurring_screen.dart';

class RecurringScreen extends StatefulWidget {
  const RecurringScreen({super.key});

  @override
  State<RecurringScreen> createState() => _RecurringScreenState();
}

class _RecurringScreenState extends State<RecurringScreen> {
  final currencyFormat = NumberFormat.currency(locale: 'vi_VN', symbol: 'đ');
  final dateFormat = DateFormat('dd/MM/yyyy');

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<RecurringProvider>(context, listen: false).loadRecurringTransactions();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Giao dịch định kỳ'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
      ),
      body: Consumer<RecurringProvider>(
        builder: (context, provider, _) {
          if (provider.isLoading && provider.recurringTransactions.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }
          
          if (provider.recurringTransactions.isEmpty) {
            return _buildEmptyState();
          }
          
          return RefreshIndicator(
            onRefresh: provider.refresh,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                // Active section
                if (provider.activeRecurrings.isNotEmpty) ...[
                  _buildSectionHeader('Đang hoạt động', provider.activeRecurrings.length),
                  ...provider.activeRecurrings.map((r) => _buildRecurringItem(r, provider)),
                ],
                
                // Inactive section
                if (provider.inactiveRecurrings.isNotEmpty) ...[
                  const SizedBox(height: 16),
                  _buildSectionHeader('Đã tạm dừng', provider.inactiveRecurrings.length),
                  ...provider.inactiveRecurrings.map((r) => _buildRecurringItem(r, provider)),
                ],
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _navigateToAddRecurring(),
        backgroundColor: AppColors.primary,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
  
  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.repeat, size: 80, color: Colors.grey[300]),
          const SizedBox(height: 16),
          const Text(
            'Chưa có giao dịch định kỳ',
            style: TextStyle(
              fontSize: 16,
              color: AppColors.textSecondary,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Nhấn + để tạo giao dịch tự động',
            style: TextStyle(color: AppColors.textHint),
          ),
        ],
      ),
    );
  }
  
  Widget _buildSectionHeader(String title, int count) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Text(
            title,
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 16,
              color: AppColors.textPrimary,
            ),
          ),
          const SizedBox(width: 8),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
            decoration: BoxDecoration(
              color: AppColors.primary.withOpacity(0.1),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Text(
              count.toString(),
              style: const TextStyle(
                color: AppColors.primary,
                fontWeight: FontWeight.bold,
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildRecurringItem(RecurringTransaction recurring, RecurringProvider provider) {
    final isExpense = recurring.type == TransactionType.EXPENSE;
    final color = isExpense ? AppColors.expense : AppColors.income;
    
    return Dismissible(
      key: Key(recurring.id.toString()),
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
      confirmDismiss: (direction) => _confirmDelete(recurring, provider),
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: recurring.isActive ? Colors.white : Colors.grey[100],
          borderRadius: BorderRadius.circular(16),
          border: recurring.isActive ? null : Border.all(color: Colors.grey[300]!),
        ),
        child: InkWell(
          onTap: () => _navigateToEditRecurring(recurring),
          borderRadius: BorderRadius.circular(16),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      width: 48,
                      height: 48,
                      decoration: BoxDecoration(
                        color: color.withOpacity(recurring.isActive ? 0.1 : 0.05),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Center(
                        child: Text(
                          recurring.categoryIcon ?? '📁',
                          style: TextStyle(
                            fontSize: 24,
                            color: recurring.isActive ? null : Colors.grey,
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            recurring.categoryName ?? 'Danh mục',
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              fontSize: 15,
                              color: recurring.isActive 
                                  ? AppColors.textPrimary 
                                  : AppColors.textSecondary,
                            ),
                          ),
                          if (recurring.description != null)
                            Text(
                              recurring.description!,
                              style: TextStyle(
                                color: AppColors.textSecondary,
                                fontSize: 12,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                        ],
                      ),
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text(
                          '${isExpense ? "-" : "+"}${currencyFormat.format(recurring.amount)}',
                          style: TextStyle(
                            color: recurring.isActive ? color : AppColors.textHint,
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                          ),
                        ),
                        Text(
                          recurring.frequencyDisplay,
                          style: const TextStyle(
                            color: AppColors.textHint,
                            fontSize: 11,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Next execution
                    if (recurring.nextExecutionDate != null)
                      Row(
                        children: [
                          const Icon(Icons.schedule, size: 14, color: AppColors.textHint),
                          const SizedBox(width: 4),
                          Text(
                            'Lần tiếp: ${dateFormat.format(recurring.nextExecutionDate!)}',
                            style: const TextStyle(
                              color: AppColors.textHint,
                              fontSize: 11,
                            ),
                          ),
                        ],
                      )
                    else
                      const SizedBox.shrink(),
                    
                    // Toggle switch
                    Switch(
                      value: recurring.isActive,
                      onChanged: (_) async {
                        await provider.toggleActive(recurring.id);
                      },
                      activeColor: AppColors.primary,
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
  
  Future<bool> _confirmDelete(RecurringTransaction recurring, RecurringProvider provider) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xóa giao dịch định kỳ'),
        content: Text('Bạn có chắc muốn xóa giao dịch "${recurring.categoryName}"?\n\n'
            'Các giao dịch đã tạo trước đó sẽ không bị ảnh hưởng.'),
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
      final success = await provider.deleteRecurring(recurring.id);
      if (success && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Đã xóa giao dịch định kỳ')),
        );
      }
      return success;
    }
    
    return false;
  }
  
  void _navigateToAddRecurring() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const AddRecurringScreen()),
    ).then((_) {
      Provider.of<RecurringProvider>(context, listen: false).loadRecurringTransactions();
    });
  }
  
  void _navigateToEditRecurring(RecurringTransaction recurring) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => AddRecurringScreen(recurring: recurring)),
    ).then((_) {
      Provider.of<RecurringProvider>(context, listen: false).loadRecurringTransactions();
    });
  }
}
