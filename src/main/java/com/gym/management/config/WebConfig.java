package com.gym.management.config;

import com.gym.management.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    
    private final AuthInterceptor authInterceptor;
    
    @Value("${app.upload.dir:/www/wwwroot/gym/uploads/avatars}")
    private String uploadDir;

    @Autowired
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保静态资源能被正确处理
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
                
        // 添加对上传目录的访问
        try {
            String uploadPath = uploadDir;
            // 移除可能的结尾斜杠以确保路径正确
            if (uploadPath.endsWith("/")) {
                uploadPath = uploadPath.substring(0, uploadPath.length() - 1);
            }
            
            // 获取上一级目录
            String parentDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
            
            logger.info("配置头像资源映射: {} -> file:{}/", "/uploads/**", parentDir);
            
            // 配置外部目录为可访问的静态资源
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + parentDir + "/");
        } catch (Exception e) {
            logger.error("配置静态资源映射出错: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/register",
                        "/api/verify-admin",
                        "/login",
                        "/logout",
                        "/perform-logout",
                        "/api/check-auth",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/uploads/**", // 排除上传的文件，无需验证权限
                        "/error"
                );
    }
} 