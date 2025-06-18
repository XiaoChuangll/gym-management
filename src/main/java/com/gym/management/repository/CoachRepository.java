package com.gym.management.repository;

import com.gym.management.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教练数据访问接口
 */
@Repository
public interface CoachRepository extends JpaRepository<Coach, String> {
    List<Coach> findByNameContaining(String name);
    
    List<Coach> findBySpecialtyContaining(String specialty);
    
    boolean existsByContact(String contact);
} 