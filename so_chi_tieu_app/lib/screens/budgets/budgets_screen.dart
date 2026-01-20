// lib/screens/budgets/budgets_screen.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_colors.dart';
import '../../models/budget.dart';
import '../../providers/budget_provider.dart';
import 'add_budget_screen.dart';

class BudgetsScreen extends StatefulWidget {
  const BudgetsScreen({super.key});

  @override
  State<BudgetsScreen> createState() => _BudgetsScreenState();
}

class _BudgetsScreenState extends State<BudgetsScreen> {
  final currencyFormat = NumberFormat.currency(locale: 'vi_VN', symbol: 'đ');

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<BudgetProvider>(context, listen: false).loadBudgets();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Ngân sách'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
      ),
      body: Consumer<BudgetProvider>(
        builder: (context, provider, _) {
          if (provider.isLoading && provider.budgets.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }
          
          return RefreshIndicator(
            onRefresh: provider.refresh,
            child: SingleChildScrollView(
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  // Month selector
                  _buildMonthSelector(provider),
                  
                  const SizedBox(height: 20),
                  
                  // Summary card
                  _buildSummaryCard(provider),
                  
                  const SizedBox(height: 20),
                  
                  // Budget list
                  if (provider.budgets.isEmpty)
                    _buildEmptyState()
                  else
                    ...provider.budgets.map((b) => _buildBudgetItem(b, provider)),
                ],
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _navigateToAddBudget(),
        backgroundColor: AppColors.primary,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
  
  Widget _buildMonthSelector(BudgetProvider provider) {
    final monthNames = [
      '', 'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
      'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'
    ];
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            icon: const Icon(Icons.chevron_left),
            onPressed: () {
              int newMonth = provider.selectedMonth - 1;
              int newYear = provider.selectedYear;
              if (newMonth < 1) {
                newMonth = 12;
                newYear--;
              }
              provider.changeMonth(newMonth, newYear);
            },
          ),
          Text(
            '${monthNames[provider.selectedMonth]} ${provider.selectedYear}',
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          IconButton(
            icon: const Icon(Icons.chevron_right),
            onPressed: () {
              int newMonth = provider.selectedMonth + 1;
              int newYear = provider.selectedYear;
              if (newMonth > 12) {
                newMonth = 1;
                newYear++;
              }
              provider.changeMonth(newMonth, newYear);
            },
          ),
        ],
      ),
    );
  }
  
  Widget _buildSummaryCard(BudgetProvider provider) {
    final percentUsed = provider.totalBudget > 0
        ? (provider.totalSpent / provider.totalBudget * 100)
        : 0.0;
    
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: percentUsed > 100
              ? [AppColors.expense, AppColors.expense.withOpacity(0.8)]
              : [AppColors.primary, AppColors.primaryDark],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        children: [
          const Text(
            'Tổng ngân sách',
            style: TextStyle(color: Colors.white70),
          ),
          const SizedBox(height: 8),
          Text(
            currencyFormat.format(provider.totalBudget),
            style: const TextStyle(
              color: Colors.white,
              fontSize: 28,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          ClipRRect(
            borderRadius: BorderRadius.circular(10),
            child: LinearProgressIndicator(
              value: (percentUsed / 100).clamp(0.0, 1.0),
              backgroundColor: Colors.white30,
              valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
              minHeight: 8,
            ),
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Đã chi: ${currencyFormat.format(provider.totalSpent)}',
                style: const TextStyle(color: Colors.white, fontSize: 12),
              ),
              Text(
                '${percentUsed.toStringAsFixed(1)}%',
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
  
  Widget _buildBudgetItem(Budget budget, BudgetProvider provider) {
    Color statusColor;
    if (budget.isOverBudget) {
      statusColor = AppColors.expense;
    } else if (budget.isWarning) {
      statusColor = AppColors.warning;
    } else {
      statusColor = AppColors.income;
    }
    
    return Dismissible(
      key: Key(budget.id.toString()),
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
      confirmDismiss: (direction) => _confirmDelete(budget, provider),
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        child: InkWell(
          onTap: () => _navigateToEditBudget(budget),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    width: 44,
                    height: 44,
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Center(
                      child: Text(
                        budget.categoryIcon ?? '📁',
                        style: const TextStyle(fontSize: 22),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          budget.categoryName ?? 'Danh mục',
                          style: const TextStyle(
                            fontWeight: FontWeight.w600,
                            fontSize: 15,
                          ),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          '${currencyFormat.format(budget.spent)} / ${currencyFormat.format(budget.amount)}',
                          style: const TextStyle(
                            color: AppColors.textSecondary,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text(
                        budget.isOverBudget ? 'Vượt quá' : 'Còn lại',
                        style: TextStyle(
                          color: statusColor,
                          fontSize: 11,
                        ),
                      ),
                      Text(
                        currencyFormat.format(budget.remaining.abs()),
                        style: TextStyle(
                          color: statusColor,
                          fontWeight: FontWeight.bold,
                          fontSize: 14,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              const SizedBox(height: 12),
              ClipRRect(
                borderRadius: BorderRadius.circular(6),
                child: LinearProgressIndicator(
                  value: (budget.percentUsed / 100).clamp(0.0, 1.0),
                  backgroundColor: Colors.grey[200],
                  valueColor: AlwaysStoppedAnimation<Color>(statusColor),
                  minHeight: 6,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
  
  Widget _buildEmptyState() {
    return Padding(
      padding: const EdgeInsets.all(40),
      child: Column(
        children: [
          Icon(Icons.account_balance_wallet, size: 80, color: Colors.grey[300]),
          const SizedBox(height: 16),
          const Text(
            'Chưa có ngân sách nào',
            style: TextStyle(
              fontSize: 16,
              color: AppColors.textSecondary,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Nhấn + để thêm ngân sách mới',
            style: TextStyle(color: AppColors.textHint),
          ),
        ],
      ),
    );
  }
  
  Future<bool> _confirmDelete(Budget budget, BudgetProvider provider) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xóa ngân sách'),
        content: Text('Bạn có chắc muốn xóa ngân sách "${budget.categoryName}"?'),
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
      await provider.deleteBudget(budget.id);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Đã xóa ngân sách')),
        );
      }
    }
    
    return result ?? false;
  }
  
  void _navigateToAddBudget() {
    final provider = Provider.of<BudgetProvider>(context, listen: false);
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => AddBudgetScreen(
          month: provider.selectedMonth,
          year: provider.selectedYear,
        ),
      ),
    ).then((_) => provider.loadBudgets());
  }
  
  void _navigateToEditBudget(Budget budget) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => AddBudgetScreen(
          budget: budget,
          month: budget.month,
          year: budget.year,
        ),
      ),
    ).then((_) {
      Provider.of<BudgetProvider>(context, listen: false).loadBudgets();
    });
  }
}
