package com.gym.management.service;

import com.gym.management.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    NotificationDTO createNotification(NotificationDTO notificationDTO, Long userId);

    List<NotificationDTO> getAllNotifications();

    List<NotificationDTO> getActiveNotifications();

    NotificationDTO updateNotificationStatus(Long id, String status);

    NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO);

    void deleteNotification(Long id);
}