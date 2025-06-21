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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
                           @RequestParam(required = false) String forceAnimation,
                           @RequestParam(required = false) String success,
                           @RequestParam(required = false) String username,
                           Model model) {
        // 如果是注册成功跳转过来的，添加成功信息
        if ("true".equals(success) && username != null) {
            model.addAttribute("registerSuccess", true);
            model.addAttribute("registerUsername", username);
        }
        
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
                            String cookieUsername = parts[0];
                            String token = parts[1];
                            
                            // 验证记住我令牌
                            if (userService.validateRememberMeToken(cookieUsername, token)) {
                                // 令牌有效，创建新会话并存储用户信息
                                HttpSession newSession = request.getSession(true);
                                newSession.setAttribute("loggedInUser", cookieUsername);
                                System.out.println("自动登录成功: " + cookieUsername);
                                
                                // 更新令牌，延长有效期
                                String newToken = userService.generateRememberMeToken(cookieUsername);
                                String cookieValue = cookieUsername + ":" + newToken;
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
                                    return "redirect:/login?autoLogin=true&username=" + cookieUsername;
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
            
            // 获取用户角色并存入session
            User user = userService.getUserDetails(username);
            if (user != null) {
                session.setAttribute("userRole", user.getRole());
            }
            
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
    public String logoutPage(HttpServletRequest request, HttpSession session) {
        // 预检查用户是否已经登出，如果已经登出则直接重定向到登录页面
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "logout";
    }
    
    // 执行退出操作
    @PostMapping("/perform-logout")
    public String performLogout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // 获取用户名
        String username = (String) session.getAttribute("loggedInUser");
        
        try {
            // 如果有用户名，清除数据库中的记住我令牌
            if (username != null) {
                userService.clearRememberMeToken(username);
            }
            
            // 清除记住我cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember-me".equals(cookie.getName())) {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        break; // 找到并处理后立即退出循环
                    }
                }
            }
            
            // 设置缓存控制头，防止浏览器缓存
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 最后清除session
            session.invalidate();
        } catch (Exception e) {
            // 记录错误但继续执行，确保用户能够登出
            System.err.println("退出登录时发生错误: " + e.getMessage());
        }
        
        // 重定向到登录页面，添加时间戳参数避免缓存
        return "redirect:/login?t=" + System.currentTimeMillis();
    }
    
    // 处理修改密码
    @PostMapping("/api/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {
        
        // 获取当前登录用户
        String username = (String) session.getAttribute("loggedInUser");
        
        // 检查用户是否登录
        if (username == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "未登录，请先登录"));
        }
        
        // 检查新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "新密码与确认密码不一致"));
        }
        
        // 调用服务修改密码
        boolean changed = userService.changePassword(username, oldPassword, newPassword);
        
        if (changed) {
            return ResponseEntity.ok()
                    .body(Map.of("success", true, "message", "密码修改成功"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "原密码不正确"));
        }
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
    
    /**
     * 获取用户密码上次修改时间
     */
    @GetMapping("/api/password-last-changed")
    @ResponseBody
    public ResponseEntity<?> getPasswordLastChanged(HttpSession session) {
        // 获取当前登录用户
        String username = (String) session.getAttribute("loggedInUser");
        
        // 检查用户是否登录
        if (username == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "未登录，请先登录"));
        }
        
        // 获取密码上次修改时间
        LocalDateTime lastChanged = userService.getPasswordLastChangedTime(username);
        
        return ResponseEntity.ok(Map.of("success", true, "lastChanged", lastChanged.toString()));
    }
    
    // 获取用户资料
    @GetMapping("/api/user-profile")
    @ResponseBody
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        // 获取当前登录用户
        String username = (String) session.getAttribute("loggedInUser");
        
        // 检查用户是否登录
        if (username == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "未登录，请先登录"));
        }
        
        // 获取用户详细信息
        User user = userService.getUserDetails(username);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("success", false, "message", "未找到用户信息"));
        }
        
        // 返回用户资料
        return ResponseEntity.ok(Map.of(
            "success", true, 
            "username", user.getUsername(),
            "displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
            "email", user.getEmail() != null ? user.getEmail() : "",
            "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
            "role", user.getRole()
        ));
    }
    
    // 更新用户基本信息
    @PostMapping("/api/update-profile")
    @ResponseBody
    public ResponseEntity<?> updateUserProfile(
            @RequestParam String displayName,
            @RequestParam(required = false) String email,
            HttpSession session) {
        
        // 获取当前登录用户
        String username = (String) session.getAttribute("loggedInUser");
        
        // 检查用户是否登录
        if (username == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "未登录，请先登录"));
        }
        
        // 更新用户信息
        boolean updated = userService.updateUserProfile(username, displayName, email);
        
        if (updated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "用户资料更新成功"));
        } else {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "用户资料更新失败"));
        }
    }
    
    // 更新用户头像
    @PostMapping("/api/update-avatar")
    @ResponseBody
    public ResponseEntity<?> updateUserAvatar(
            @RequestParam String avatarUrl,
            HttpSession session) {
        
        // 获取当前登录用户
        String username = (String) session.getAttribute("loggedInUser");
        
        // 检查用户是否登录
        if (username == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "未登录，请先登录"));
        }
        
        // 验证头像数据
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "头像数据不能为空"));
        }
        
        // 验证Base64数据格式
        if (!avatarUrl.startsWith("data:image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "无效的图像数据格式"));
        }
        
        // 限制图像数据大小（约1MB的Base64大小）
        if (avatarUrl.length() > 1000000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "头像图像太大，请使用更小的图像"));
        }
        
        // 更新用户头像
        boolean updated = userService.updateUserAvatar(username, avatarUrl);
        
        if (updated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "用户头像更新成功"));
        } else {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "用户头像更新失败"));
        }
    }
    
    // API: 验证管理员身份
    @PostMapping("/api/verify-admin")
    @ResponseBody
    public ResponseEntity<?> verifyAdmin(
            @RequestParam String username,
            @RequestParam String password) {
        
        // 验证用户名和密码是否正确
        if (!userService.validateUser(username, password)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "用户名或密码错误"));
        }
        
        // 验证用户是否是管理员角色
        User user = userService.getUserDetails(username);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "该账号没有管理员权限"));
        }
        
        // 验证通过，返回成功信息
        return ResponseEntity.ok()
                .body(Map.of("success", true, "message", "管理员身份验证成功"));
    }
    
    // 显示注册页面
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    // 处理注册请求
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String adminUsername,
                          @RequestParam String adminPassword,
                          @RequestParam(required = false) String displayName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String avatarUrl) {
        
        // 首先验证管理员身份
        if (!userService.validateUser(adminUsername, adminPassword)) {
            return "redirect:/register?error=" + URLEncoder.encode("管理员账号或密码错误", StandardCharsets.UTF_8);
        }
        
        // 验证管理员是否有权限
        User adminUser = userService.getUserDetails(adminUsername);
        if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
            return "redirect:/register?error=" + URLEncoder.encode("该账号无管理员权限", StandardCharsets.UTF_8);
        }
        
        // 验证两次密码输入是否一致
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=" + URLEncoder.encode("两次输入的密码不一致", StandardCharsets.UTF_8);
        }
        
        // 检查用户名是否已存在 - 这部分已经在前端处理，这里作为后备验证
        if (userService.existsByUsername(username)) {
            return "redirect:/register?error=" + URLEncoder.encode("用户名已存在", StandardCharsets.UTF_8);
        }
        
        // 创建新用户对象，角色设置为USER
        User newUser = new User(username, password, "USER");
        
        // 设置可选字段
        if (displayName != null && !displayName.isEmpty()) {
            newUser.setDisplayName(displayName);
        }
        
        if (email != null && !email.isEmpty()) {
            newUser.setEmail(email);
        }
        
        // 设置默认头像
        newUser.setAvatarUrl("/uploads/avatars/default.jpg");
        
        // 保存用户
        User savedUser = userService.saveUser(newUser);
        
        // 如果提供了头像数据，保存头像
        if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image/")) {
            userService.updateUserAvatar(username, avatarUrl);
        }
        
        // 注册成功，重定向到登录页面并显示成功动画
        return "redirect:/login?success=true&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
    }

    // API: 检查用户名是否已存在
    @GetMapping("/api/check-username")
    @ResponseBody
    public ResponseEntity<?> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}