package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程出勤率数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAttendanceDTO {
    private String courseId;
    private String courseName;
    private Integer totalReservations;
    private Float attendanceRate;
} 