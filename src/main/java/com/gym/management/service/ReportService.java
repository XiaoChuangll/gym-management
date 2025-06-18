package com.gym.management.service;

import com.gym.management.dto.CoachWorkloadDTO;
import com.gym.management.dto.CourseAttendanceDTO;
import com.gym.management.dto.MemberReservationDTO;

import java.util.List;

/**
 * 报表服务接口
 */
public interface ReportService {
    /**
     * 获取会员的预约情况
     * @param memberId 会员ID
     * @return 会员预约情况列表
     */
    List<MemberReservationDTO> getMemberReservations(String memberId);
    
    /**
     * 获取课程出勤率统计
     * @return 课程出勤率列表
     */
    List<CourseAttendanceDTO> getCourseAttendance();
    
    /**
     * 获取教练工作量统计
     * @return 教练工作量列表
     */
    List<CoachWorkloadDTO> getCoachWorkload();
} 