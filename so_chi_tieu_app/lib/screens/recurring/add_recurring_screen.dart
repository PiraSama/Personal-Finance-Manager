// lib/screens/recurring/add_recurring_screen.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_colors.dart';
import '../../models/recurring_transaction.dart';
import '../../models/category.dart';
import '../../providers/recurring_provider.dart';

class AddRecurringScreen extends StatefulWidget {
  final RecurringTransaction? recurring;
  
  const AddRecurringScreen({super.key, this.recurring});

  @override
  State<AddRecurringScreen> createState() => _AddRecurringScreenState();
}

class _AddRecurringScreenState extends State<AddRecurringScreen> {
  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _descriptionController = TextEditingController();
  
  TransactionType _selectedType = TransactionType.EXPENSE;
  Category? _selectedCategory;
  RecurringFrequency _selectedFrequency = RecurringFrequency.MONTHLY;
  DateTime _startDate = DateTime.now();
  DateTime? _endDate;
  bool _isLoading = false;
  
  bool get isEditing => widget.recurring != null;

  @override
  void initState() {
    super.initState();
    
    if (isEditing) {
      final r = widget.recurring!;
      _amountController.text = r.amount.toStringAsFixed(0);
      _descriptionController.text = r.description ?? '';
      _selectedType = r.type;
      _selectedFrequency = r.frequency;
      _startDate = r.startDate;
      _endDate = r.endDate;
    }
    
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final provider = Provider.of<RecurringProvider>(context, listen: false);
      if (provider.categories.isEmpty) {
        provider.loadRecurringTransactions();
      } else if (isEditing) {
        setState(() {
          _selectedCategory = provider.categories
              .where((c) => c.id == widget.recurring!.categoryId)
              .firstOrNull;
        });
      }
    });
  }

  @override
  void dispose() {
    _amountController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedCategory == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Vui lòng chọn danh mục')),
      );
      return;
    }
    
    setState(() => _isLoading = true);
    
    final recurring = RecurringTransaction(
      id: widget.recurring?.id ?? 0,
      amount: double.parse(_amountController.text.replaceAll(',', '')),
      type: _selectedType,
      description: _descriptionController.text.isEmpty 
          ? null 
          : _descriptionController.text,
      categoryId: _selectedCategory!.id,
      frequency: _selectedFrequency,
      startDate: _startDate,
      endDate: _endDate,
    );
    
    final provider = Provider.of<RecurringProvider>(context, listen: false);
    bool success;
    
    if (isEditing) {
      success = await provider.updateRecurring(widget.recurring!.id, recurring);
    } else {
      success = await provider.addRecurring(recurring);
    }
    
    setState(() => _isLoading = false);
    
    if (success && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(isEditing ? 'Đã cập nhật giao dịch định kỳ' : 'Đã tạo giao dịch định kỳ'),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text(isEditing ? 'Sửa giao dịch định kỳ' : 'Tạo giao dịch định kỳ'),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            // Type selector
            _buildTypeSelector(),
            
            const SizedBox(height: 24),
            
            // Amount field
            _buildAmountField(),
            
            const SizedBox(height: 16),
            
            // Category selector
            _buildCategorySelector(),
            
            const SizedBox(height: 16),
            
            // Frequency selector
            _buildFrequencySelector(),
            
            const SizedBox(height: 16),
            
            // Date pickers
            _buildDatePickers(),
            
            const SizedBox(height: 16),
            
            // Description field
            _buildDescriptionField(),
            
            const SizedBox(height: 32),
            
            // Save button
            _buildSaveButton(),
          ],
        ),
      ),
    );
  }
  
  Widget _buildTypeSelector() {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Expanded(
            child: _buildTypeButton(
              type: TransactionType.EXPENSE,
              label: 'Chi tiêu',
              icon: Icons.trending_down,
              color: AppColors.expense,
            ),
          ),
          Expanded(
            child: _buildTypeButton(
              type: TransactionType.INCOME,
              label: 'Thu nhập',
              icon: Icons.trending_up,
              color: AppColors.income,
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildTypeButton({
    required TransactionType type,
    required String label,
    required IconData icon,
    required Color color,
  }) {
    final isSelected = _selectedType == type;
    
    return GestureDetector(
      onTap: () {
        setState(() {
          _selectedType = type;
          _selectedCategory = null;
        });
      },
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.1) : Colors.transparent,
          borderRadius: BorderRadius.circular(10),
          border: isSelected ? Border.all(color: color, width: 2) : null,
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: isSelected ? color : AppColors.textHint),
            const SizedBox(width: 8),
            Text(
              label,
              style: TextStyle(
                color: isSelected ? color : AppColors.textSecondary,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildAmountField() {
    return TextFormField(
      controller: _amountController,
      keyboardType: TextInputType.number,
      style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
      textAlign: TextAlign.center,
      decoration: InputDecoration(
        hintText: '0',
        hintStyle: TextStyle(
          color: AppColors.textHint,
          fontSize: 28,
          fontWeight: FontWeight.bold,
        ),
        suffixText: 'đ',
        suffixStyle: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide.none,
        ),
        filled: true,
        fillColor: Colors.white,
      ),
      validator: (value) {
        if (value == null || value.isEmpty) return 'Vui lòng nhập số tiền';
        final amount = double.tryParse(value.replaceAll(',', ''));
        if (amount == null || amount <= 0) return 'Số tiền phải lớn hơn 0';
        return null;
      },
    );
  }
  
  Widget _buildCategorySelector() {
    return Consumer<RecurringProvider>(
      builder: (context, provider, _) {
        final categories = _selectedType == TransactionType.INCOME
            ? provider.incomeCategories
            : provider.expenseCategories;
        
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Danh mục',
              style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: categories.isEmpty
                  ? const Center(
                      child: Padding(
                        padding: EdgeInsets.all(20),
                        child: CircularProgressIndicator(),
                      ),
                    )
                  : Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: categories.map((category) {
                        final isSelected = _selectedCategory?.id == category.id;
                        return GestureDetector(
                          onTap: () => setState(() => _selectedCategory = category),
                          child: Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                            decoration: BoxDecoration(
                              color: isSelected
                                  ? AppColors.primary.withOpacity(0.1)
                                  : Colors.grey[100],
                              borderRadius: BorderRadius.circular(20),
                              border: isSelected
                                  ? Border.all(color: AppColors.primary, width: 2)
                                  : null,
                            ),
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text(category.icon ?? '📁', style: const TextStyle(fontSize: 16)),
                                const SizedBox(width: 6),
                                Text(
                                  category.name,
                                  style: TextStyle(
                                    color: isSelected ? AppColors.primary : AppColors.textPrimary,
                                    fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      }).toList(),
                    ),
            ),
          ],
        );
      },
    );
  }
  
  Widget _buildFrequencySelector() {
    final frequencies = [
      (RecurringFrequency.DAILY, 'Hàng ngày'),
      (RecurringFrequency.WEEKLY, 'Hàng tuần'),
      (RecurringFrequency.MONTHLY, 'Hàng tháng'),
      (RecurringFrequency.YEARLY, 'Hàng năm'),
    ];
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Tần suất',
          style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
        ),
        const SizedBox(height: 8),
        Container(
          padding: const EdgeInsets.all(4),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
          ),
          child: Row(
            children: frequencies.map((f) {
              final isSelected = _selectedFrequency == f.$1;
              return Expanded(
                child: GestureDetector(
                  onTap: () => setState(() => _selectedFrequency = f.$1),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    decoration: BoxDecoration(
                      color: isSelected ? AppColors.primary : Colors.transparent,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Center(
                      child: Text(
                        f.$2,
                        style: TextStyle(
                          color: isSelected ? Colors.white : AppColors.textSecondary,
                          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                          fontSize: 11,
                        ),
                      ),
                    ),
                  ),
                ),
              );
            }).toList(),
          ),
        ),
      ],
    );
  }
  
  Widget _buildDatePickers() {
    final dateFormat = DateFormat('dd/MM/yyyy');
    
    return Row(
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Ngày bắt đầu',
                style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
              ),
              const SizedBox(height: 8),
              InkWell(
                onTap: () async {
                  final date = await showDatePicker(
                    context: context,
                    initialDate: _startDate,
                    firstDate: DateTime.now(),
                    lastDate: DateTime.now().add(const Duration(days: 365 * 5)),
                  );
                  if (date != null) setState(() => _startDate = date);
                },
                child: Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.calendar_today, size: 18, color: AppColors.primary),
                      const SizedBox(width: 8),
                      Text(dateFormat.format(_startDate)),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Ngày kết thúc',
                style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
              ),
              const SizedBox(height: 8),
              InkWell(
                onTap: () async {
                  final date = await showDatePicker(
                    context: context,
                    initialDate: _endDate ?? _startDate.add(const Duration(days: 30)),
                    firstDate: _startDate,
                    lastDate: DateTime.now().add(const Duration(days: 365 * 10)),
                  );
                  setState(() => _endDate = date);
                },
                child: Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.calendar_today, size: 18, color: AppColors.textHint),
                      const SizedBox(width: 8),
                      Text(
                        _endDate != null ? dateFormat.format(_endDate!) : 'Không giới hạn',
                        style: TextStyle(
                          color: _endDate != null ? AppColors.textPrimary : AppColors.textHint,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
  
  Widget _buildDescriptionField() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Ghi chú (tùy chọn)',
          style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
        ),
        const SizedBox(height: 8),
        TextFormField(
          controller: _descriptionController,
          maxLines: 2,
          decoration: InputDecoration(
            hintText: 'Nhập ghi chú...',
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: Colors.white,
          ),
        ),
      ],
    );
  }
  
  Widget _buildSaveButton() {
    return ElevatedButton(
      onPressed: _isLoading ? null : _save,
      style: ElevatedButton.styleFrom(
        backgroundColor: _selectedType == TransactionType.EXPENSE
            ? AppColors.expense
            : AppColors.income,
        padding: const EdgeInsets.symmetric(vertical: 16),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: _isLoading
          ? const SizedBox(
              height: 20,
              width: 20,
              child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
            )
          : Text(
              isEditing ? 'Cập nhật' : 'Tạo giao dịch định kỳ',
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
            ),
    );
  }
}
