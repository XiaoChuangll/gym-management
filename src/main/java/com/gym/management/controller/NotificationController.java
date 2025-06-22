package com.gym.management.controller;

import com.gym.management.dto.NotificationDTO;
import com.gym.management.service.NotificationService;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // 创建新通知 - 仅管理员可用
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notificationDTO, HttpSession session) {
        // 检查用户是否登录
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录");
        }

        // 检查用户角色
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以发布通知");
        }

        // 获取用户ID
        Long userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("无法获取用户信息");
        }

        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    // 获取所有通知
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // 获取活跃通知
    @GetMapping("/active")
    public ResponseEntity<List<NotificationDTO>> getActiveNotifications() {
        List<NotificationDTO> notifications = notificationService.getActiveNotifications();
        return ResponseEntity.ok(notifications);
    }

    // 更新通知 - 仅管理员可用
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO, HttpSession session) {
        // 检查用户是否登录
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录");
        }

        // 检查用户角色
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以更新通知");
        }

        NotificationDTO updatedNotification = notificationService.updateNotification(id, notificationDTO);
        return ResponseEntity.ok(updatedNotification);
    }

    // 删除通知 - 仅管理员可用
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id, HttpSession session) {
        // 检查用户是否登录
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录");
        }

        // 检查用户角色
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以删除通知");
        }

        notificationService.deleteNotification(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "通知已成功删除");
        return ResponseEntity.ok(response);
    }
}