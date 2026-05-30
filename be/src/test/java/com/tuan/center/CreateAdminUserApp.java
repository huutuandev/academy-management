package com.tuan.center;

import com.tuan.center.entity.Role;
import com.tuan.center.entity.User;
import com.tuan.center.repository.RoleRepository;
import com.tuan.center.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateAdminUserApp {

    public static void main(String[] args) {
        // Khởi chạy Spring Boot context để nạp các Bean và kết nối Database
        ConfigurableApplicationContext context = SpringApplication.run(CenterManagementApplication.class, args);
        
        try {
            UserRepository userRepository = context.getBean(UserRepository.class);
            RoleRepository roleRepository = context.getBean(RoleRepository.class);
            PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

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
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo tài khoản admin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng context để kết thúc chương trình
            context.close();
        }
    }
}
