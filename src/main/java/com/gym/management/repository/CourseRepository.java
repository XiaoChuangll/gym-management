package com.gym.management.repository;

import com.gym.management.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程数据访问接口
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByCoachCoachId(String coachId);
    
    List<Course> findByCourseNameContaining(String courseName);
    
    @Query("SELECT c FROM Course c WHERE c.courseTime >= :startTime AND c.courseTime <= :endTime")
    List<Course> findByCourseTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
} 