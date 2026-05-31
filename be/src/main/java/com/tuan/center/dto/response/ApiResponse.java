package com.tuan.center.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;
    private Long timestamp;

    // Constructor mặc định
    public ApiResponse() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Constructor đầy đủ
    public ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // ==================== STATIC FACTORY METHODS ====================

    // Success
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "Thành công", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data);
    }

    // Error với HttpStatus (rất tiện dụng)
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(false, status.value(), message, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return new ApiResponse<>(false, status.value(), message, data);
    }

    // Error với int code (giữ lại để tương thích cũ)
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    // Error đơn giản
    public static <T> ApiResponse<T> error(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }
}