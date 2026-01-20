package com.thotran.sochitieu.service;

import com.thotran.sochitieu.dto.request.UserCreateRequest;
import com.thotran.sochitieu.dto.request.UserUpdateRequest;
import com.thotran.sochitieu.dto.response.UserResponse;
import com.thotran.sochitieu.entity.User;
import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import com.thotran.sochitieu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến User.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultCategoryInitializer defaultCategoryInitializer;
    
    /**
     * Đăng ký user mới
     */
    @Transactional
    public UserResponse register(UserCreateRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Tạo user mới
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Mã hóa password
                .fullName(request.getFullName())
                .build();
        
        // Lưu vào DB
        user = userRepository.save(user);
        
        // Tạo các danh mục mặc định cho user mới
        defaultCategoryInitializer.createDefaultCategories(user);
        
        return mapToResponse(user);
    }
    
    /**
     * Lấy thông tin user theo ID
     */
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        return mapToResponse(user);
    }
    
    /**
     * Lấy user entity theo ID (internal use)
     */
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    
    /**
     * Cập nhật thông tin user
     */
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        user.setFullName(request.getFullName());
        user = userRepository.save(user);
        
        return mapToResponse(user);
    }
    
    // === Helper method: Convert Entity -> DTO ===
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
