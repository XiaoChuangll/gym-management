package com.gym.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 会员数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String memberId;
    private String name;
    private String contact;
    private LocalDate joinDate;
    private String memberType;
} 