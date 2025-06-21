package com.gym.management.controller;

import com.gym.management.model.User;
import com.gym.management.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        // 检查是否已登录
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            return "redirect:/"; // 已登录则重定向到首页
        }
        
        // 检查是否存在记住我的Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    // 尝试从Cookie解析用户名
                    try {
                        String decodedValue = new String(Base64.getDecoder().decode(cookie.getValue()));
                        String[] parts = decodedValue.split(":");
                        if (parts.length == 2) {
                            String username = parts[0];
                            String token = parts[1];
                            
                            // 验证记住我令牌
                            if (userService.validateRememberMeToken(username, token)) {
                                // 令牌有效，直接登录
                                HttpSession newSession = request.getSession();
                                newSession.setAttribute("loggedInUser", username);
                                return "redirect:/";
                            }
                        }
                    } catch (Exception e) {
                        // 解码失败，忽略这个cookie
                    }
                }
            }
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (userService.validateUser(username, password)) {
            // 登录成功，将用户信息存入session
            session.setAttribute("loggedInUser", username);
            
            // 处理"记住我"功能
            if (rememberMe != null && rememberMe.equals("on")) {
                // 先清除旧的记住我cookie
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("remember-me".equals(cookie.getName())) {
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }
                
                // 生成记住我令牌
                String token = userService.generateRememberMeToken(username);
                
                // 创建记住我Cookie
                String cookieValue = username + ":" + token;
                String encodedValue = Base64.getEncoder().encodeToString(cookieValue.getBytes());
                
                // 创建Cookie，设置有效期为30天
                Cookie rememberMeCookie = new Cookie("remember-me", encodedValue);
                rememberMeCookie.setMaxAge(60 * 60 * 24 * 30); // 30天有效期
                rememberMeCookie.setPath("/");
                response.addCookie(rememberMeCookie);
            } else {
                // 如果不需要记住我，清除可能存在的记住我token
                userService.clearRememberMeToken(username);
                
                // 清除可能存在的记住我cookie
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("remember-me".equals(cookie.getName())) {
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }
            }
            
            return "redirect:/";
        } else {
            // 登录失败
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login";
        }
    }

    // 显示退出确认页面
    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }
    
    // 执行退出操作
    @PostMapping("/perform-logout")
    public String performLogout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // 获取用户名
        String username = (String) session.getAttribute("loggedInUser");
        
        // 清除session
        session.invalidate();
        
        // 清除所有cookies，确保完全退出
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 特别处理记住我cookie
                if ("remember-me".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        
        // 如果有用户名，清除数据库中的记住我令牌
        if (username != null) {
            userService.clearRememberMeToken(username);
        }
        
        // 重定向到登录页面
        return "redirect:/login";
    }

    @GetMapping("/api/check-auth")
    @ResponseBody
    public ResponseEntity<?> checkAuth(HttpSession session) {
        return session.getAttribute("loggedInUser") != null ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(401).build();
    }
}