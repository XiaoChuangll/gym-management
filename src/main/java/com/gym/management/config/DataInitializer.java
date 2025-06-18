package com.gym.management.config;

import com.gym.management.model.User;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在默认管理员账号
        if (!userService.existsByUsername("admin")) {
            // 创建默认管理员账号
            User admin = new User("admin", "admin123", "ADMIN");
            userService.saveUser(admin);
            System.out.println("已创建默认管理员账号: admin / admin123");
        }
        
        // 检查是否已存在默认用户账号
        if (!userService.existsByUsername("user")) {
            // 创建默认用户账号
            User user = new User("user", "user123", "USER");
            userService.saveUser(user);
            System.out.println("已创建默认用户账号: user / user123");
        }
    }
} 