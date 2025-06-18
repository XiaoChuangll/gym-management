package com.gym.management.repository;

import com.gym.management.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预约数据访问接口
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByMemberMemberId(String memberId);
    
    List<Reservation> findByCourseCourseId(String courseId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.course.courseId = :courseId")
    Integer countByCourseId(@Param("courseId") String courseId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.course.coach.coachId = :coachId")
    Integer countByCoachId(@Param("coachId") String coachId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.course.courseId = :courseId AND r.status = '已完成'")
    Integer countCompletedByCourseId(@Param("courseId") String courseId);
} 