package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.UserCreateRequest;
import com.thotran.sochitieu.dto.request.UserUpdateRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.UserResponse;
import com.thotran.sochitieu.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho User APIs.
 * Base path: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * POST /api/users/register
     * Đăng ký user mới
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserCreateRequest request) {
        
        UserResponse user = userService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công!", user));
    }
    
    /**
     * GET /api/users/{id}
     * Lấy thông tin user theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        
        UserResponse user = userService.getById(id);
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * PUT /api/users/{id}
     * Cập nhật thông tin user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        
        UserResponse user = userService.update(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công!", user));
    }
}
