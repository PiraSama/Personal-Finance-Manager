package com.thotran.sochitieu.exception;

/**
 * Enum chứa tất cả mã lỗi của ứng dụng.
 * Mỗi mã lỗi có HTTP status code và message tương ứng.
 */
public enum ErrorCode {
    // === Lỗi chung ===
    UNCATEGORIZED_EXCEPTION(500, "Lỗi không xác định"),
    INVALID_INPUT(400, "Dữ liệu đầu vào không hợp lệ"),
    
    // === Lỗi liên quan đến User ===
    USER_NOT_FOUND(404, "Không tìm thấy người dùng"),
    USER_EXISTED(409, "Email đã được sử dụng"),
    INVALID_PASSWORD(400, "Mật khẩu phải có ít nhất 6 ký tự"),
    
    // === Lỗi liên quan đến Category ===
    CATEGORY_NOT_FOUND(404, "Không tìm thấy danh mục"),
    CATEGORY_NAME_EXISTED(409, "Tên danh mục đã tồn tại"),
    
    // === Lỗi liên quan đến Transaction ===
    TRANSACTION_NOT_FOUND(404, "Không tìm thấy giao dịch"),
    INVALID_AMOUNT(400, "Số tiền phải lớn hơn 0"),
    
    // === Lỗi liên quan đến Budget ===
    BUDGET_NOT_FOUND(404, "Không tìm thấy ngân sách"),
    BUDGET_EXISTED(409, "Ngân sách cho danh mục này trong tháng đã tồn tại"),
    
    // === Lỗi xác thực ===
    UNAUTHORIZED(401, "Chưa đăng nhập"),
    FORBIDDEN(403, "Không có quyền truy cập");
    
    private final int httpStatusCode;
    private final String message;
    
    ErrorCode(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }
    
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
    
    public String getMessage() {
        return message;
    }
}
