package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.LoginRequest;
import com.thotran.sochitieu.dto.request.UserCreateRequest;
import com.thotran.sochitieu.dto.response.AuthResponse;
import com.thotran.sochitieu.dto.response.UserResponse;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.NotificationRepository;
import com.thotran.sochitieu.repository.UserRepository;
import com.thotran.sochitieu.security.JwtTokenProvider;
import com.thotran.sochitieu.entity.Notification;
import com.thotran.sochitieu.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý logic đăng nhập/đăng ký.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultCategoryInitializer defaultCategoryInitializer;
    private final NotificationRepository notificationRepository;
    
    /**
     * Đăng nhập và trả về JWT token
     */
    public AuthResponse login(LoginRequest request) {
        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 
                        "Email hoặc mật khẩu không đúng"));
        
        // Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, 
                    "Email hoặc mật khẩu không đúng");
        }
        
        // Tạo JWT token
        String token = jwtTokenProvider.generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .user(mapToUserResponse(user))
                .build();
    }
    
    /**
     * Đăng ký user mới và trả về JWT token
     */
    @Transactional
    public AuthResponse register(UserCreateRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Tạo user mới
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
        
        // Lưu vào DB
        user = userRepository.save(user);
        
        // Tạo các danh mục mặc định
        defaultCategoryInitializer.createDefaultCategories(user);
        
        // Gửi thông báo chào mừng
        sendWelcomeNotification(user);
        
        // Tạo JWT token
        String token = jwtTokenProvider.generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .user(mapToUserResponse(user))
                .build();
    }
    
    /**
     * Gửi thông báo chào mừng
     */
    private void sendWelcomeNotification(User user) {
        Notification notification = Notification.builder()
                .type(NotificationType.WELCOME)
                .title("Chào mừng bạn đến với Sổ Chi Tiêu! 🎉")
                .message("Hãy bắt đầu ghi chép chi tiêu hàng ngày để quản lý tài chính tốt hơn nhé!")
                .user(user)
                .build();
        notificationRepository.save(notification);
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
