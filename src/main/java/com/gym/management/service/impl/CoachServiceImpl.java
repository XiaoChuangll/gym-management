package com.gym.management.service.impl;

import com.gym.management.dto.CoachDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Coach;
import com.gym.management.repository.CoachRepository;
import com.gym.management.service.CoachService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教练服务实现类
 */
@Service
@Transactional
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    @Autowired
    public CoachServiceImpl(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Override
    public CoachDTO createCoach(CoachDTO coachDTO) {
        Coach coach = new Coach();
        BeanUtils.copyProperties(coachDTO, coach);
        coach = coachRepository.save(coach);
        BeanUtils.copyProperties(coach, coachDTO);
        return coachDTO;
    }

    @Override
    public List<CoachDTO> getAllCoaches() {
        return coachRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CoachDTO getCoachById(String coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("教练不存在，ID: " + coachId));
        return convertToDTO(coach);
    }

    @Override
    public CoachDTO updateCoach(String coachId, CoachDTO coachDTO) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("教练不存在，ID: " + coachId));
        
        coach.setName(coachDTO.getName());
        coach.setContact(coachDTO.getContact());
        coach.setSpecialty(coachDTO.getSpecialty());
        
        coach = coachRepository.save(coach);
        return convertToDTO(coach);
    }

    @Override
    public void deleteCoach(String coachId) {
        if (!coachRepository.existsById(coachId)) {
            throw new ResourceNotFoundException("教练不存在，ID: " + coachId);
        }
        coachRepository.deleteById(coachId);
    }

    @Override
    public List<CoachDTO> findCoachesByName(String name) {
        return coachRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachDTO> findCoachesBySpecialty(String specialty) {
        return coachRepository.findBySpecialtyContaining(specialty).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     * @param coach 教练实体
     * @return 教练DTO
     */
    private CoachDTO convertToDTO(Coach coach) {
        CoachDTO coachDTO = new CoachDTO();
        BeanUtils.copyProperties(coach, coachDTO);
        return coachDTO;
    }
} 