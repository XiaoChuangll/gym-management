package com.gym.management.repository;

import com.gym.management.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(String status);

    List<Notification> findByStatusOrderByCreatedAtDesc(String status);

    List<Notification> findAllByOrderByCreatedAtDesc();
}