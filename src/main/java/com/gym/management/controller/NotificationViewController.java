package com.gym.management.controller;

import com.gym.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/notifications")
public class NotificationViewController {

    @Autowired
    private NotificationService notificationService;

    // 显示通知列表页面
    @GetMapping
    public String showNotificationList(Model model) {
        model.addAttribute("notifications", notificationService.getActiveNotifications());
        return "notification/list";
    }

    // 显示通知管理页面 - 仅管理员可用
    @GetMapping("/manage")
    public String showNotificationManagement(HttpSession session, Model model) {
        // 检查用户是否是管理员
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }

        return "notification/manage";
    }
}