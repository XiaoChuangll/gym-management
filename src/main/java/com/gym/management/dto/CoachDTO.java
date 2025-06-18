package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教练数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachDTO {
    private String coachId;
    private String name;
    private String contact;
    private String specialty;
} 