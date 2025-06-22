package com.gym.management.service.impl;

import com.gym.management.dto.NotificationDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Notification;
import com.gym.management.model.User;
import com.gym.management.repository.NotificationRepository;
import com.gym.management.repository.UserRepository;
import com.gym.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setStatus("active");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setCreatedBy(user);

        // 设置新增字段
        notification.setPriority(notificationDTO.getPriority());
        notification.setTarget(notificationDTO.getTarget());
        notification.setExpiryDate(notificationDTO.getExpiryDate());

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Override
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getActiveNotifications() {
        return notificationRepository.findByStatus("active").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO updateNotificationStatus(Long id, String status) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在"));

        notification.setStatus(status);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    public NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在"));

        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        if (notificationDTO.getStatus() != null) {
            notification.setStatus(notificationDTO.getStatus());
        }
        notification.setPriority(notificationDTO.getPriority());
        notification.setTarget(notificationDTO.getTarget());
        notification.setExpiryDate(notificationDTO.getExpiryDate());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在"));

        notificationRepository.delete(notification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setStatus(notification.getStatus());
        dto.setCreatedAt(notification.getCreatedAt());
        if (notification.getCreatedBy() != null) {
            dto.setCreatedById(notification.getCreatedBy().getId());
            dto.setCreatedByUsername(notification.getCreatedBy().getUsername());
        }

        // 设置新增字段
        dto.setPriority(notification.getPriority());
        dto.setTarget(notification.getTarget());
        dto.setExpiryDate(notification.getExpiryDate());

        return dto;
    }
}