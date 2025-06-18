package com.gym.management.service;

import com.gym.management.dto.CoachDTO;

import java.util.List;

/**
 * 教练服务接口
 */
public interface CoachService {
    /**
     * 创建教练
     * @param coachDTO 教练DTO
     * @return 创建的教练DTO
     */
    CoachDTO createCoach(CoachDTO coachDTO);
    
    /**
     * 获取所有教练
     * @return 教练DTO列表
     */
    List<CoachDTO> getAllCoaches();
    
    /**
     * 根据ID获取教练
     * @param coachId 教练ID
     * @return 教练DTO
     */
    CoachDTO getCoachById(String coachId);
    
    /**
     * 更新教练信息
     * @param coachId 教练ID
     * @param coachDTO 教练DTO
     * @return 更新后的教练DTO
     */
    CoachDTO updateCoach(String coachId, CoachDTO coachDTO);
    
    /**
     * 删除教练
     * @param coachId 教练ID
     */
    void deleteCoach(String coachId);
    
    /**
     * 根据姓名查询教练
     * @param name 教练姓名
     * @return 教练DTO列表
     */
    List<CoachDTO> findCoachesByName(String name);
    
    /**
     * 根据专长查询教练
     * @param specialty 专长
     * @return 教练DTO列表
     */
    List<CoachDTO> findCoachesBySpecialty(String specialty);
} 