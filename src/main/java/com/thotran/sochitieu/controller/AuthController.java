package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.request.LoginRequest;
import com.thotran.sochitieu.dto.request.UserCreateRequest;
import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.AuthResponse;
import com.thotran.sochitieu.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Authentication APIs.
 * Base path: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * POST /api/auth/login
     * Đăng nhập và nhận JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        AuthResponse response = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công!", response));
    }
    
    /**
     * POST /api/auth/register
     * Đăng ký user mới và nhận JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody UserCreateRequest request) {
        
        AuthResponse response = authService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công!", response));
    }
}
