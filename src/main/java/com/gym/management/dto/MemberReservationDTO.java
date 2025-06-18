package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会员预约报表数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberReservationDTO {
    private String courseId;
    private String courseName;
    private LocalDateTime courseTime;
    private LocalDateTime reservationTime;
    private String status;
} 