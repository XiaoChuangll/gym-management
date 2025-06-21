package com.gym.management.service.impl;

import com.gym.management.model.User;
import com.gym.management.repository.UserRepository;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean validateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.isPresent() && userOptional.get().getPassword().equals(password);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public String generateRememberMeToken(String username) {
        // 生成一个随机令牌
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String token = Base64.getEncoder().encodeToString(randomBytes);
        
        try {
            // 创建一个简单的哈希 (用户名 + 当前时间戳 + 随机字节)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String dataToHash = username + System.currentTimeMillis() + token;
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            token = Base64.getEncoder().encodeToString(hashBytes);
            
            // 保存令牌到用户记录中
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setRememberMeToken(token);
                userRepository.save(user);
            }
            
            return token;
        } catch (NoSuchAlgorithmException e) {
            // 如果SHA-256不可用，则使用简单令牌
            return token;
        }
    }
    
    @Override
    public boolean validateRememberMeToken(String username, String token) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 验证令牌是否匹配
            return token.equals(user.getRememberMeToken());
        }
        return false;
    }

    @Override
    public void clearRememberMeToken(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 清空记住我令牌
            user.setRememberMeToken(null);
            userRepository.save(user);
        }
    }
    
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 验证旧密码是否正确
            if (user.getPassword().equals(oldPassword)) {
                // 设置新密码
                user.setPassword(newPassword);
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public LocalDateTime getPasswordLastChangedTime(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            LocalDateTime changedAt = user.getPasswordChangedAt();
            
            // 如果没有记录密码修改时间，返回用户创建时间或当前时间
            return changedAt != null ? changedAt : LocalDateTime.now();
        }
        
        // 如果用户不存在，返回当前时间
        return LocalDateTime.now();
    }
} 