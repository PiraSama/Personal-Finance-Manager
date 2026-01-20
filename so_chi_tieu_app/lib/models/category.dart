// lib/models/category.dart

enum TransactionType { INCOME, EXPENSE }

class Category {
  final int id;
  final String name;
  final TransactionType type;
  final String? icon;
  final String? color;

  Category({
    required this.id,
    required this.name,
    required this.type,
    this.icon,
    this.color,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['id'],
      name: json['name'],
      type: json['type'] == 'INCOME' 
          ? TransactionType.INCOME 
          : TransactionType.EXPENSE,
      icon: json['icon'],
      color: json['color'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'type': type == TransactionType.INCOME ? 'INCOME' : 'EXPENSE',
      'icon': icon,
      'color': color,
    };
  }
  
  // Helper to get color as Flutter Color
  int get colorValue {
    if (color == null) return 0xFF6366F1;
    return int.parse(color!.replaceFirst('#', '0xFF'));
  }
}
