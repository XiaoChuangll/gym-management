package com.gym.management.service;

import com.gym.management.dto.ReservationDTO;

import java.util.List;

/**
 * 预约服务接口
 */
public interface ReservationService {
    /**
     * 创建预约
     * @param reservationDTO 预约DTO
     * @return 创建的预约DTO
     */
    ReservationDTO createReservation(ReservationDTO reservationDTO);
    
    /**
     * 获取所有预约
     * @return 预约DTO列表
     */
    List<ReservationDTO> getAllReservations();
    
    /**
     * 根据ID获取预约
     * @param reservationId 预约ID
     * @return 预约DTO
     */
    ReservationDTO getReservationById(String reservationId);
    
    /**
     * 更新预约状态
     * @param reservationId 预约ID
     * @param status 状态
     * @return 更新后的预约DTO
     */
    ReservationDTO updateReservationStatus(String reservationId, String status);
    
    /**
     * 删除预约
     * @param reservationId 预约ID
     */
    void deleteReservation(String reservationId);
    
    /**
     * 根据会员ID获取预约
     * @param memberId 会员ID
     * @return 预约DTO列表
     */
    List<ReservationDTO> getReservationsByMemberId(String memberId);
    
    /**
     * 根据课程ID获取预约
     * @param courseId 课程ID
     * @return 预约DTO列表
     */
    List<ReservationDTO> getReservationsByCourseId(String courseId);
    
    /**
     * 搜索预约
     * @param query 搜索关键词
     * @return 预约DTO列表
     */
    List<ReservationDTO> searchReservations(String query);
} 