package com.gym.management.service;

import com.gym.management.model.User;

import java.util.Optional;

public interface UserService {
    
    User saveUser(User user);
    
    Optional<User> findByUsername(String username);
    
    boolean validateUser(String username, String password);
    
    boolean existsByUsername(String username);
} 