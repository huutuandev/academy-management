package com.tuan.center.service.impl;

import com.tuan.center.entity.Role;
import com.tuan.center.entity.User;
import com.tuan.center.dto.request.LoginRequest;
import com.tuan.center.dto.request.RegisterRequest;
import com.tuan.center.dto.response.AuthResponse;
import com.tuan.center.exception.UnauthorizedException;
import com.tuan.center.repository.RoleRepository;
import com.tuan.center.repository.UserRepository;
import com.tuan.center.security.jwt.JwtService;
import com.tuan.center.security.user.CustomUserDetails;
import com.tuan.center.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements com.tuan.center.service.AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Xác thực thông tin đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Lấy User entity từ CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 3. Tạo access token
        String accessToken = jwtService.generateAccessToken(user);
        String tokenId = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefreshToken(user, tokenId);
        refreshTokenService.saveRefreshToken(user.getId(), tokenId, refreshToken);
        // 4. Tạo AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' đã tồn tại!");
        }

        // 2. Kiểm tra email đã tồn tại chưa (nếu có)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email '" + request.getEmail() + "' đã tồn tại!");
            }
        }

        // 3. Lấy hoặc tạo Role
        String roleName = request.getRole() != null && !request.getRole().trim().isEmpty()
                ? request.getRole().toUpperCase()
                : "USER";

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription("Default " + roleName + " role");
                    return roleRepository.save(newRole);
                });

        // 4. Tạo User entity mới
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // 5. Tạo access token cho user vừa đăng ký
        String accessToken = jwtService.generateAccessToken(user);

        // 6. Trả về AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(UUID.randomUUID().toString())
                .userId(user.getId())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {

        Claims claims =
                jwtService.extractAllClaims(refreshToken);

        Long userId =
                Long.parseLong(claims.getSubject());

        String tokenId =
                claims.get("jti", String.class);

        if (userId == null) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }
        String storedToken =
                refreshTokenService.getRefreshToken(
                        userId,
                        tokenId
                );

        if (storedToken == null) {
            throw new UnauthorizedException("Hết hạn token");
        }

        if (!storedToken.equals(refreshToken)) {
            throw new UnauthorizedException("Hết hạn token");
        }

        // Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        Claims claims =
                jwtService.extractAllClaims(refreshToken);

        Long userId =
                Long.parseLong(claims.getSubject());

        String tokenId =
                claims.get("jti", String.class);

        refreshTokenService.deleteRefreshToken(
                userId,
                tokenId
        );
    }
}
