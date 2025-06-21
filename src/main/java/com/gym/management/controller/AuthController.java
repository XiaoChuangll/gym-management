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
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(required = false) String autoLogin,
                           @RequestParam(required = false) String forceAnimation) {
        // 如果已经在进行自动登录动画显示，则直接返回登录页面
        if ("true".equals(autoLogin)) {
            return "login";
        }
        
        // 如果不是强制显示动画，并且用户已登录，则重定向到首页
        HttpSession session = request.getSession(false);
        if (!"true".equals(forceAnimation) && session != null && session.getAttribute("loggedInUser") != null) {
            return "redirect:/index"; // 已登录则重定向到首页
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
                                // 令牌有效，创建新会话并存储用户信息
                                HttpSession newSession = request.getSession(true);
                                newSession.setAttribute("loggedInUser", username);
                                System.out.println("自动登录成功: " + username);
                                
                                // 更新令牌，延长有效期
                                String newToken = userService.generateRememberMeToken(username);
                                String cookieValue = username + ":" + newToken;
                                String encodedValue = Base64.getEncoder().encodeToString(cookieValue.getBytes());
                                
                                Cookie rememberMeCookie = new Cookie("remember-me", encodedValue);
                                rememberMeCookie.setMaxAge(60 * 60 * 24 * 30); // 30天有效期
                                rememberMeCookie.setPath("/"); // 确保cookie对整个应用可用
                                // 不设置Secure标志，除非确定是HTTPS连接
                                rememberMeCookie.setSecure(request.isSecure());
                                // 不设置SameSite属性，因为移动浏览器可能不支持
                                // 设置HTTPOnly以提高安全性，但允许JavaScript在必要时访问
                                rememberMeCookie.setHttpOnly(false);
                                response.addCookie(rememberMeCookie);
                                
                                // 如果是强制显示动画或者首次检测到cookie，显示登录动画
                                // 特殊处理根路径访问时的自动登录动画
                                String referer = request.getHeader("Referer");
                                boolean isDirectAccess = referer == null || !referer.contains(request.getServerName());
                                
                                if ("true".equals(forceAnimation) || isDirectAccess) {
                                    return "redirect:/login?autoLogin=true&username=" + username;
                                } else {
                                    // 如果是从站内页面访问登录页，则直接跳转到首页
                                    return "redirect:/index";
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 解码失败，忽略这个cookie
                    }
                }
            }
        }
        
        // 如果没有有效的记住我cookie，也没有有效的会话，显示登录页面
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        @RequestParam(required = false) String showAnimation,
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
                rememberMeCookie.setPath("/"); // 确保cookie对整个应用可用
                // 不设置Secure标志，除非确定是HTTPS连接
                rememberMeCookie.setSecure(request.isSecure());
                // 不设置SameSite属性，因为移动浏览器可能不支持
                // 设置HTTPOnly以提高安全性，但允许JavaScript在必要时访问
                rememberMeCookie.setHttpOnly(false);
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
            
            // 无论是否请求显示动画，都显示登录成功动画
            // 这样所有登录操作都会先显示动画
            return "redirect:/login?autoLogin=true&username=" + username;
            
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
    public ResponseEntity<?> checkAuth(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // 首先检查会话是否有登录用户
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            return ResponseEntity.ok()
                .body(Map.of("status", "logged_in", "username", username));
        }
        
        // 会话中没有登录用户，尝试检查remember-me cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    try {
                        // 尝试从Cookie解析用户名
                        String decodedValue = new String(Base64.getDecoder().decode(cookie.getValue()));
                        String[] parts = decodedValue.split(":");
                        if (parts.length == 2) {
                            String cookieUsername = parts[0];
                            String token = parts[1];
                            
                            // 验证记住我令牌
                            if (userService.validateRememberMeToken(cookieUsername, token)) {
                                // 令牌有效，创建新会话
                                session.setAttribute("loggedInUser", cookieUsername);
                                
                                // 更新令牌
                                String newToken = userService.generateRememberMeToken(cookieUsername);
                                String cookieValue = cookieUsername + ":" + newToken;
                                String encodedValue = Base64.getEncoder().encodeToString(cookieValue.getBytes());
                                
                                Cookie rememberMeCookie = new Cookie("remember-me", encodedValue);
                                rememberMeCookie.setMaxAge(60 * 60 * 24 * 30);
                                rememberMeCookie.setPath("/");
                                rememberMeCookie.setSecure(request.isSecure());
                                rememberMeCookie.setHttpOnly(false);
                                response.addCookie(rememberMeCookie);
                                
                                return ResponseEntity.ok()
                                    .body(Map.of(
                                        "status", "auto_login_success", 
                                        "username", cookieUsername,
                                        "message", "自动登录成功"
                                    ));
                            } else {
                                // 令牌无效
                                return ResponseEntity.status(401)
                                    .body(Map.of("status", "invalid_token", "message", "无效的记住我令牌"));
                            }
                        }
                    } catch (Exception e) {
                        // 解码失败
                        return ResponseEntity.status(401)
                            .body(Map.of("status", "invalid_cookie_format", "message", "Cookie格式无效"));
                    }
                }
            }
        }
        
        // 没有找到有效的登录信息
        return ResponseEntity.status(401)
            .body(Map.of("status", "not_logged_in", "message", "用户未登录"));
    }
}