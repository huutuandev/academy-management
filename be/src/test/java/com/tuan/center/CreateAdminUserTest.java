package com.tuan.center;

import com.tuan.center.entity.Role;
import com.tuan.center.entity.User;
import com.tuan.center.repository.RoleRepository;
import com.tuan.center.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CreateAdminUserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Commit // Đảm bảo lưu dữ liệu xuống database thật
    void createAdminUser() {
        // 1. Tìm hoặc tạo role ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("Administrator role");
                    return roleRepository.save(role);
                });

        // 2. Tạo tài khoản admin nếu chưa tồn tại
        String adminUsername = "admin";
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@center.com")
                    .phone("0123456789")
                    .role(adminRole)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
            System.out.println("\n===============================================");
            System.out.println("====== ĐÃ TẠO TÀI KHOẢN ADMIN THÀNH CÔNG ======");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("===============================================\n");
        } else {
            System.out.println("\nTài khoản admin đã tồn tại trong hệ thống!\n");
        }
    }
}
