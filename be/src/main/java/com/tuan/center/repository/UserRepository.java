package com.tuan.center.repository;

import com.tuan.center.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    <Optional>User findByUsername(String username);
}
