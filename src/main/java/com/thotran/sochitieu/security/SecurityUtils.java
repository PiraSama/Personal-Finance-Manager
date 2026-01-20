package com.thotran.sochitieu.security;

import com.thotran.sochitieu.exception.AppException;
import com.thotran.sochitieu.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class để lấy thông tin user hiện tại từ SecurityContext.
 */
public class SecurityUtils {
    
    private SecurityUtils() {
        // Private constructor - utility class
    }
    
    /**
     * Lấy userId của user đang đăng nhập từ SecurityContext
     * 
     * @return userId
     * @throws AppException nếu chưa đăng nhập
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    
    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof Long;
    }
}
