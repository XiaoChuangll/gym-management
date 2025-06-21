package com.gym.management.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // 定义不需要拦截的路径列表
    private final List<String> publicPaths = Arrays.asList(
            "/register",
            "/api/verify-admin",
            "/login",
            "/logout",
            "/perform-logout",
            "/api/check-auth",
            "/about"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String path = request.getServletPath();
        
        // 检查是否为公共访问路径
        for (String publicPath : publicPaths) {
            if (path.equals(publicPath) || path.startsWith("/css/") || path.startsWith("/js/") || 
                path.startsWith("/img/") || path.startsWith("/uploads/")) {
                return true; // 是公共路径，直接放行
            }
        }
        
        // 获取session
        HttpSession session = request.getSession();
        
        // 检查用户是否已登录
        Object user = session.getAttribute("loggedInUser");
        
        if (user == null) {
            // 用户未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        // 用户已登录，继续请求
        return true;
    }
} 