package com.gym.management.service.impl;

import com.gym.management.dto.ReservationDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Course;
import com.gym.management.model.Member;
import com.gym.management.model.Reservation;
import com.gym.management.repository.CourseRepository;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.ReservationRepository;
import com.gym.management.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约服务实现类
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, 
                                 MemberRepository memberRepository, 
                                 CourseRepository courseRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        
        // 设置会员
        Member member = memberRepository.findById(reservationDTO.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("会员不存在，ID: " + reservationDTO.getMemberId()));
        reservation.setMember(member);
        
        // 设置课程
        Course course = courseRepository.findById(reservationDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + reservationDTO.getCourseId()));
        reservation.setCourse(course);
        
        // 设置预约时间和状态
        reservation.setReservationTime(reservationDTO.getReservationTime());
        reservation.setStatus(reservationDTO.getStatus());
        
        // 保存预约
        reservation = reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    @Override
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO getReservationById(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在，ID: " + reservationId));
        return convertToDTO(reservation);
    }

    @Override
    public ReservationDTO updateReservationStatus(String reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在，ID: " + reservationId));
        
        reservation.setStatus(status);
        reservation = reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    @Override
    public void deleteReservation(String reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("预约不存在，ID: " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }

    @Override
    public List<ReservationDTO> getReservationsByMemberId(String memberId) {
        return reservationRepository.findByMemberMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getReservationsByCourseId(String courseId) {
        return reservationRepository.findByCourseCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     * @param reservation 预约实体
     * @return 预约DTO
     */
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setReservationId(reservation.getReservationId());
        reservationDTO.setMemberId(reservation.getMember().getMemberId());
        reservationDTO.setMemberName(reservation.getMember().getName());
        reservationDTO.setCourseId(reservation.getCourse().getCourseId());
        reservationDTO.setCourseName(reservation.getCourse().getCourseName());
        reservationDTO.setReservationTime(reservation.getReservationTime());
        reservationDTO.setStatus(reservation.getStatus());
        return reservationDTO;
    }
} 