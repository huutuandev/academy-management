package com.tuan.center.controller;

import com.tuan.center.dto.request.LoginRequest;
import com.tuan.center.dto.request.RegisterRequest;
import com.tuan.center.dto.response.ApiResponse;
import com.tuan.center.dto.response.AuthResponse;
import com.tuan.center.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    @Value("${app.cookie.path}")
    private String cookiePath;

    @Value("${app.cookie.max-age}")
    private long cookieMaxAge;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(request);
            ResponseCookie refreshToken = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(sameSite)
                    .path(cookiePath)
                    .maxAge(cookieMaxAge)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());

            AuthResponse responseData = AuthResponse.builder()
                    .accessToken(authResponse.getAccessToken())
                    .userId(authResponse.getUserId())
                    .fullName(authResponse.getFullName())
                    .avatarUrl(authResponse.getAvatarUrl())
                    .build();


            return ResponseEntity.ok(
                    ApiResponse.success("Đăng nhập thành công", responseData)
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success("Đăng ký tài khoản thành công", response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Không tìm thấy refresh token"));
            }

            // AuthService xử lý refresh
            AuthResponse authResponse = authService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(
                    ApiResponse.success("Làm mới token thành công",
                            authResponse)
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            authService.logout(refreshToken);

            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path(cookiePath)
                    .maxAge(0)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

            return ResponseEntity.ok(
                    ApiResponse.success("Đăng xuất thành công", null)
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ApiResponse.error("Có lỗi khi đăng xuất")
            );
        }
    }
}
