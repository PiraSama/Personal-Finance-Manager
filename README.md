# 💰 Personal Finance Manager (Sổ Chi Tiêu)

Ứng dụng quản lý tài chính cá nhân với **Spring Boot Backend** và **Flutter Mobile/Web App**.

## 🛠️ Công nghệ sử dụng

### Backend
- Java 17 + Spring Boot 3.x
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate
- MySQL Database

### Frontend (Flutter)
- Flutter 3.x
- Provider (State Management)
- Dio (HTTP Client)
- FL Chart (Biểu đồ)

## ✨ Tính năng

- ✅ Đăng nhập / Đăng ký với JWT
- ✅ Dashboard với thống kê thời gian thực
- ✅ Quản lý giao dịch (Thu/Chi) - CRUD
- ✅ Thống kê với Pie Chart & Bar Chart
- ✅ Quản lý ngân sách theo danh mục
- ✅ Hệ thống thông báo
- ✅ Quản lý danh mục tùy chỉnh
- ✅ Giao dịch định kỳ tự động

## 🚀 Cài đặt

### Backend
```bash
# Clone repo
git clone https://github.com/PiraSama/Personal-Finance-Manager.git
cd Personal-Finance-Manager

# Cấu hình database trong application.properties
# Chạy app
mvn spring-boot:run
```

### Flutter App
```bash
cd so_chi_tieu_app
flutter pub get
flutter run -d windows  # hoặc chrome, android
```

## 📱 Screenshots

Coming soon...

## 📝 API Endpoints

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | /api/auth/register | Đăng ký |
| POST | /api/auth/login | Đăng nhập |
| GET | /api/categories | Danh sách danh mục |
| GET/POST | /api/transactions | Quản lý giao dịch |
| GET/POST | /api/budgets | Quản lý ngân sách |
| GET | /api/notifications | Thông báo |
| GET/POST | /api/recurring-transactions | Giao dịch định kỳ |

## 👨‍💻 Tác giả

- PiraSama

## 📄 License

MIT License
