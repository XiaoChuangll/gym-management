package com.gym.management.service.impl;

import com.gym.management.model.User;
import com.gym.management.repository.UserRepository;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();
    
    @Value("${app.upload.dir:/www/wwwroot/gym/uploads/avatars}")
    private String uploadDir;
    
    @Value("${app.upload.url:/uploads/avatars}")
    private String uploadUrl;

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
    
    @Override
    public boolean updateUserProfile(String username, String displayName, String email) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 更新显示名称和邮箱
            if (displayName != null && !displayName.isEmpty()) {
                user.setDisplayName(displayName);
            }
            
            if (email != null) {
                user.setEmail(email);
            }
            
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean updateUserAvatar(String username, String avatarUrl) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 检查是否是Base64编码的图像数据
            if (avatarUrl != null && avatarUrl.startsWith("data:image/")) {
                try {
                    // 保存Base64图像到文件
                    String filePath = saveAvatarToFile(username, avatarUrl);
                    
                    // 数据库中只保存文件路径
                    user.setAvatarUrl(filePath);
                    userRepository.save(user);
                    return true;
                } catch (Exception e) {
                    logger.error("头像保存失败: " + e.getMessage(), e);
                    e.printStackTrace();
                    return false;
                }
            } else if (avatarUrl != null) {
                // 如果不是Base64数据，可能是已经保存的路径，直接保存
                user.setAvatarUrl(avatarUrl);
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public User getUserDetails(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }
    
    /**
     * 将Base64编码的图像保存为文件
     * 
     * @param username 用户名，用于生成唯一文件名
     * @param base64Image Base64编码的图像数据
     * @return 保存的文件路径
     */
    private String saveAvatarToFile(String username, String base64Image) throws Exception {
        File directory = new File(uploadDir);
        logger.info("准备保存头像到目录: " + uploadDir);
        
        try {
            // 检查目录是否存在
            if (!directory.exists()) {
                logger.info("目录不存在，尝试创建目录: " + uploadDir);
                boolean created = directory.mkdirs();
                if (!created) {
                    logger.error("创建目录失败: " + uploadDir);
                    // 检查上级目录是否存在且有权限
                    File parentDir = directory.getParentFile();
                    if (parentDir != null) {
                        logger.info("检查上级目录: " + parentDir.getAbsolutePath() + 
                                   ", 存在: " + parentDir.exists() + 
                                   ", 可写: " + parentDir.canWrite());
                    }
                    throw new IOException("无法创建目录: " + uploadDir);
                }
                logger.info("目录创建成功: " + uploadDir);
            }
            
            // 检查目录权限
            if (!directory.canWrite()) {
                logger.error("目录没有写入权限: " + uploadDir);
                throw new IOException("目录没有写入权限: " + uploadDir);
            }
            
            // 提取图像类型和数据
            String[] parts = base64Image.split(",");
            String imageType = "png"; // 默认为png
            String base64Data;
            
            if (parts.length > 1) {
                String mimeType = parts[0];
                if (mimeType.contains("image/")) {
                    imageType = mimeType.split("/")[1].split(";")[0];
                }
                
                base64Data = parts[1];
            } else {
                base64Data = base64Image; // 尝试直接解析
                logger.warn("Base64数据格式可能不正确，尝试直接解析");
            }
            
            try {
                // 解码Base64数据
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                
                // 创建唯一文件名 (用户名 + 时间戳)
                String fileName = username + "_" + System.currentTimeMillis() + "." + imageType;
                String filePath = uploadDir + "/" + fileName;
                
                logger.info("准备写入文件: " + filePath + ", 数据大小: " + imageBytes.length + " 字节");
                
                // 创建临时文件对象检查是否可写
                File testFile = new File(filePath);
                if (testFile.exists()) {
                    boolean canOverwrite = testFile.delete();
                    if (!canOverwrite) {
                        logger.error("无法覆盖已存在的文件: " + filePath);
                        throw new IOException("无法覆盖已存在的文件");
                    }
                }
                
                // 使用FileOutputStream直接写入文件，提供更多错误信息
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
                    fos.write(imageBytes);
                    fos.flush();
                    logger.info("头像保存成功: " + filePath);
                }
                
                // 返回相对URL路径，以便从网页访问
                return uploadUrl + "/" + fileName;
            } catch (IllegalArgumentException e) {
                logger.error("Base64解码失败: " + e.getMessage(), e);
                throw new IllegalArgumentException("无效的Base64数据: " + e.getMessage());
            } catch (java.io.IOException e) {
                logger.error("文件写入失败: " + e.getMessage(), e);
                throw new IOException("文件写入失败: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("头像保存过程中发生错误: " + e.getMessage(), e);
            throw e;
        }
    }
} 