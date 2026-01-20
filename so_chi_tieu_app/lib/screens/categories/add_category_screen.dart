// lib/screens/categories/add_category_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/constants/app_colors.dart';
import '../../models/category.dart';
import '../../providers/category_provider.dart';

class AddCategoryScreen extends StatefulWidget {
  final Category? category;
  final TransactionType initialType;
  
  const AddCategoryScreen({
    super.key, 
    this.category,
    this.initialType = TransactionType.EXPENSE,
  });

  @override
  State<AddCategoryScreen> createState() => _AddCategoryScreenState();
}

class _AddCategoryScreenState extends State<AddCategoryScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  
  late TransactionType _selectedType;
  String _selectedIcon = '📁';
  bool _isLoading = false;
  
  bool get isEditing => widget.category != null;
  
  // Danh sách icons để chọn
  final List<String> _availableIcons = [
    '🍔', '🍕', '🍜', '🍲', '☕',
    '🚗', '🚌', '✈️', '⛽', '🚕',
    '🏠', '💡', '💧', '📱', '💻',
    '👕', '👗', '👟', '💄', '💍',
    '🎮', '🎬', '🎵', '📚', '🏋️',
    '💊', '🏥', '💉', '🩺', '🧴',
    '🎁', '💰', '💵', '💳', '📈',
    '🏦', '📦', '🛒', '🛍️', '📝',
  ];

  @override
  void initState() {
    super.initState();
    
    if (isEditing) {
      _nameController.text = widget.category!.name;
      _selectedType = widget.category!.type;
      _selectedIcon = widget.category!.icon ?? '📁';
    } else {
      _selectedType = widget.initialType;
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    
    setState(() => _isLoading = true);
    
    final category = Category(
      id: widget.category?.id ?? 0,
      name: _nameController.text.trim(),
      type: _selectedType,
      icon: _selectedIcon,
    );
    
    final provider = Provider.of<CategoryProvider>(context, listen: false);
    bool success;
    
    if (isEditing) {
      success = await provider.updateCategory(widget.category!.id, category);
    } else {
      success = await provider.addCategory(category);
    }
    
    setState(() => _isLoading = false);
    
    if (success && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(isEditing ? 'Đã cập nhật danh mục' : 'Đã thêm danh mục'),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text(isEditing ? 'Sửa danh mục' : 'Thêm danh mục'),
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
            
            // Icon selector
            _buildIconSelector(),
            
            const SizedBox(height: 24),
            
            // Name field
            _buildNameField(),
            
            const SizedBox(height: 32),
            
            // Save button
            _buildSaveButton(),
          ],
        ),
      ),
    );
  }
  
  Widget _buildTypeSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Loại danh mục',
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: AppColors.textPrimary,
          ),
        ),
        const SizedBox(height: 8),
        Container(
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
        ),
      ],
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
        });
      },
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.1) : Colors.transparent,
          borderRadius: BorderRadius.circular(10),
          border: isSelected 
              ? Border.all(color: color, width: 2) 
              : null,
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
  
  Widget _buildIconSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Biểu tượng',
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: AppColors.textPrimary,
          ),
        ),
        const SizedBox(height: 8),
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: _availableIcons.map((icon) {
              final isSelected = _selectedIcon == icon;
              return GestureDetector(
                onTap: () {
                  setState(() {
                    _selectedIcon = icon;
                  });
                },
                child: Container(
                  width: 44,
                  height: 44,
                  decoration: BoxDecoration(
                    color: isSelected
                        ? AppColors.primary.withOpacity(0.1)
                        : Colors.grey[100],
                    borderRadius: BorderRadius.circular(10),
                    border: isSelected
                        ? Border.all(color: AppColors.primary, width: 2)
                        : null,
                  ),
                  child: Center(
                    child: Text(
                      icon,
                      style: const TextStyle(fontSize: 22),
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
  
  Widget _buildNameField() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Tên danh mục',
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: AppColors.textPrimary,
          ),
        ),
        const SizedBox(height: 8),
        TextFormField(
          controller: _nameController,
          decoration: InputDecoration(
            hintText: 'Nhập tên danh mục...',
            prefixIcon: Container(
              padding: const EdgeInsets.all(12),
              child: Text(
                _selectedIcon,
                style: const TextStyle(fontSize: 22),
              ),
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: Colors.white,
          ),
          validator: (value) {
            if (value == null || value.trim().isEmpty) {
              return 'Vui lòng nhập tên danh mục';
            }
            return null;
          },
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
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
      ),
      child: _isLoading
          ? const SizedBox(
              height: 20,
              width: 20,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                color: Colors.white,
              ),
            )
          : Text(
              isEditing ? 'Cập nhật' : 'Lưu danh mục',
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
    );
  }
}
