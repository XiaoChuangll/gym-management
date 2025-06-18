package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教练工作量数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachWorkloadDTO {
    private String coachId;
    private String coachName;
    private Integer totalCourses;
    private Integer totalReservations;
} 