package com.tuan.center.service.impl;

import com.tuan.center.entity.Role;
import com.tuan.center.entity.User;
import com.tuan.center.dto.request.LoginRequest;
import com.tuan.center.dto.request.RegisterRequest;
import com.tuan.center.dto.response.AuthResponse;
import com.tuan.center.repository.RoleRepository;
import com.tuan.center.repository.UserRepository;
import com.tuan.center.security.jwt.JwtService;
import com.tuan.center.security.user.CustomUserDetails;
import com.tuan.center.service.IAuthService;
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
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

        // 4. Tạo AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(UUID.randomUUID().toString())
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
}
