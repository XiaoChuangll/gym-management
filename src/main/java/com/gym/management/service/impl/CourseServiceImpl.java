package com.gym.management.service.impl;

import com.gym.management.dto.CourseDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Coach;
import com.gym.management.model.Course;
import com.gym.management.repository.CoachRepository;
import com.gym.management.repository.CourseRepository;
import com.gym.management.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CoachRepository coachRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CoachRepository coachRepository) {
        this.courseRepository = courseRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        
        // 设置基本属性
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseTime(courseDTO.getCourseTime());
        course.setCapacity(courseDTO.getCapacity());
        
        // 设置教练
        Coach coach = coachRepository.findById(courseDTO.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("教练不存在，ID: " + courseDTO.getCoachId()));
        course.setCoach(coach);
        
        course = courseRepository.save(course);
        return convertToDTO(course);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + courseId));
        return convertToDTO(course);
    }

    @Override
    public CourseDTO updateCourse(String courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + courseId));
        
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseTime(courseDTO.getCourseTime());
        course.setCapacity(courseDTO.getCapacity());
        
        // 如果教练ID发生变化，则更新教练
        if (!course.getCoach().getCoachId().equals(courseDTO.getCoachId())) {
            Coach coach = coachRepository.findById(courseDTO.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("教练不存在，ID: " + courseDTO.getCoachId()));
            course.setCoach(coach);
        }
        
        course = courseRepository.save(course);
        return convertToDTO(course);
    }

    @Override
    public void deleteCourse(String courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("课程不存在，ID: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    @Override
    public List<CourseDTO> getCoursesByCoachId(String coachId) {
        return courseRepository.findByCoachCoachId(coachId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> findCoursesByName(String courseName) {
        return courseRepository.findByCourseNameContaining(courseName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> findCoursesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return courseRepository.findByCourseTimeBetween(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     * @param course 课程实体
     * @return 课程DTO
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setCourseTime(course.getCourseTime());
        courseDTO.setCapacity(course.getCapacity());
        courseDTO.setCoachId(course.getCoach().getCoachId());
        courseDTO.setCoachName(course.getCoach().getName());
        return courseDTO;
    }
} 