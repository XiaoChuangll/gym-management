package com.gym.management.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(name = "display_name")
    private String displayName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
    
    @Column
    private String rememberMeToken;
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    
    @Column
    private String email;
    
    @Column(name = "avatar_url")
    private String avatarUrl;

    // 默认构造函数
    public User() {
        // 设置初始密码修改时间为当前时间
        this.passwordChangedAt = LocalDateTime.now();
    }

    // 带参数的构造函数
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.passwordChangedAt = LocalDateTime.now();
        // 默认将登录名作为显示名称
        this.displayName = username;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        // 更新密码时自动更新修改时间
        this.passwordChangedAt = LocalDateTime.now();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getRememberMeToken() {
        return rememberMeToken;
    }
    
    public void setRememberMeToken(String rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
    }
    
    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }
    
    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
} 