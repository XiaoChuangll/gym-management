package com.gym.management.service.impl;

import com.gym.management.dto.CoachWorkloadDTO;
import com.gym.management.dto.CourseAttendanceDTO;
import com.gym.management.dto.MemberReservationDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Coach;
import com.gym.management.model.Course;
import com.gym.management.model.Reservation;
import com.gym.management.repository.CoachRepository;
import com.gym.management.repository.CourseRepository;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.ReservationRepository;
import com.gym.management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 */
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CoachRepository coachRepository;

    @Autowired
    public ReportServiceImpl(ReservationRepository reservationRepository,
                           MemberRepository memberRepository,
                           CourseRepository courseRepository,
                           CoachRepository coachRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    public List<MemberReservationDTO> getMemberReservations(String memberId) {
        // 检查会员是否存在
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("会员不存在，ID: " + memberId);
        }
        
        // 获取会员的所有预约
        List<Reservation> reservations = reservationRepository.findByMemberMemberId(memberId);
        
        // 转换为DTO
        return reservations.stream()
                .map(reservation -> {
                    MemberReservationDTO dto = new MemberReservationDTO();
                    dto.setCourseId(reservation.getCourse().getCourseId());
                    dto.setCourseName(reservation.getCourse().getCourseName());
                    dto.setCourseTime(reservation.getCourse().getCourseTime());
                    dto.setReservationTime(reservation.getReservationTime());
                    dto.setStatus(reservation.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseAttendanceDTO> getCourseAttendance() {
        List<CourseAttendanceDTO> result = new ArrayList<>();
        
        // 获取所有课程
        List<Course> courses = courseRepository.findAll();
        
        for (Course course : courses) {
            CourseAttendanceDTO dto = new CourseAttendanceDTO();
            dto.setCourseId(course.getCourseId());
            dto.setCourseName(course.getCourseName());
            
            // 获取课程的预约总数
            Integer totalReservations = reservationRepository.countByCourseId(course.getCourseId());
            dto.setTotalReservations(totalReservations);
            
            // 获取已完成的预约数量
            Integer completedReservations = reservationRepository.countCompletedByCourseId(course.getCourseId());
            
            // 计算出勤率
            float attendanceRate = totalReservations > 0 ? (float) completedReservations / totalReservations : 0f;
            dto.setAttendanceRate(attendanceRate);
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public List<CoachWorkloadDTO> getCoachWorkload() {
        List<CoachWorkloadDTO> result = new ArrayList<>();
        
        // 获取所有教练
        List<Coach> coaches = coachRepository.findAll();
        
        // 创建教练ID到课程数量的映射
        Map<String, Integer> coachCourseCount = new HashMap<>();
        for (Course course : courseRepository.findAll()) {
            String coachId = course.getCoach().getCoachId();
            coachCourseCount.put(coachId, coachCourseCount.getOrDefault(coachId, 0) + 1);
        }
        
        for (Coach coach : coaches) {
            CoachWorkloadDTO dto = new CoachWorkloadDTO();
            dto.setCoachId(coach.getCoachId());
            dto.setCoachName(coach.getName());
            
            // 设置教练的课程数量
            dto.setTotalCourses(coachCourseCount.getOrDefault(coach.getCoachId(), 0));
            
            // 获取教练的预约总数
            Integer totalReservations = reservationRepository.countByCoachId(coach.getCoachId());
            dto.setTotalReservations(totalReservations != null ? totalReservations : 0);
            
            result.add(dto);
        }
        
        return result;
    }
} 