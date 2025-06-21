package com.gym.management.config;

import com.gym.management.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/register", "/about", "/login", "/logout", "/perform-logout", 
                        "/api/verify-admin", // 添加验证管理员API
                        "/uploads/**", // 允许访问上传文件
                        "/css/**", "/js/**", "/img/**"); // 不拦截登录相关和静态资源
    }
} 