package com.gym.management.service;

import com.gym.management.model.User;

import java.time.LocalDateTime;
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
    
    // 清除用户的记住我令牌
    void clearRememberMeToken(String username);
    
    // 修改用户密码
    boolean changePassword(String username, String oldPassword, String newPassword);
    
    // 获取用户密码上次修改时间
    LocalDateTime getPasswordLastChangedTime(String username);
    
    // 更新用户基本资料（显示名称、邮箱）
    boolean updateUserProfile(String username, String displayName, String email);
    
    // 更新用户头像
    boolean updateUserAvatar(String username, String avatarUrl);
    
    // 获取用户完整信息
    User getUserDetails(String username);
} 