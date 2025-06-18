package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String courseId;
    private String courseName;
    private String coachId;
    private String coachName;
    private LocalDateTime courseTime;
    private Integer capacity;
} 