package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private String reservationId;
    private String memberId;
    private String memberName;
    private String courseId;
    private String courseName;
    private LocalDateTime reservationTime;
    private String status;
} 