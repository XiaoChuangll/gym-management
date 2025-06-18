package com.gym.management.service;

import com.gym.management.dto.CourseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    /**
     * 创建课程
     * @param courseDTO 课程DTO
     * @return 创建的课程DTO
     */
    CourseDTO createCourse(CourseDTO courseDTO);
    
    /**
     * 获取所有课程
     * @return 课程DTO列表
     */
    List<CourseDTO> getAllCourses();
    
    /**
     * 根据ID获取课程
     * @param courseId 课程ID
     * @return 课程DTO
     */
    CourseDTO getCourseById(String courseId);
    
    /**
     * 更新课程信息
     * @param courseId 课程ID
     * @param courseDTO 课程DTO
     * @return 更新后的课程DTO
     */
    CourseDTO updateCourse(String courseId, CourseDTO courseDTO);
    
    /**
     * 删除课程
     * @param courseId 课程ID
     */
    void deleteCourse(String courseId);
    
    /**
     * 根据教练ID获取课程
     * @param coachId 教练ID
     * @return 课程DTO列表
     */
    List<CourseDTO> getCoursesByCoachId(String coachId);
    
    /**
     * 根据课程名称查询课程
     * @param courseName 课程名称
     * @return 课程DTO列表
     */
    List<CourseDTO> findCoursesByName(String courseName);
    
    /**
     * 根据时间范围查询课程
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 课程DTO列表
     */
    List<CourseDTO> findCoursesByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
} 