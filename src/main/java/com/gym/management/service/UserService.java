package com.gym.management.service;

import com.gym.management.model.User;

import java.util.Optional;

public interface UserService {
    
    User saveUser(User user);
    
    Optional<User> findByUsername(String username);
    
    boolean validateUser(String username, String password);
    
    boolean existsByUsername(String username);
    
    // 生成记住我令牌
    String generateRememberMeToken(String username);
    
    // 验证记住我令牌是否有效
    boolean validateRememberMeToken(String username, String token);
} 