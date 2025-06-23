package com.gym.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 课程数据传输对象
 */
public class CourseDTO {
    private String courseId;
    private String courseName;
    private String coachId;
    private String coachName;
    private LocalDateTime courseTime;
    private Integer capacity;
    // 当前预约人数
    private Integer currentReservations;
    private Integer percent;
    // 添加用于进度条显示的属性
    private Integer attendancePercent;
    // 添加当前出席人数属性（与currentReservations相同，但命名更符合首页展示）
    private Integer currentAttendees;

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCoachId() { return coachId; }
    public void setCoachId(String coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public LocalDateTime getCourseTime() { return courseTime; }
    public void setCourseTime(LocalDateTime courseTime) { this.courseTime = courseTime; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getCurrentReservations() { return currentReservations; }
    public void setCurrentReservations(Integer currentReservations) { this.currentReservations = currentReservations; }

    public Integer getPercent() { return percent; }
    public void setPercent(Integer percent) { this.percent = percent; }
    
    public Integer getAttendancePercent() { return attendancePercent; }
    public void setAttendancePercent(Integer attendancePercent) { this.attendancePercent = attendancePercent; }
    
    public Integer getCurrentAttendees() { 
        // 如果未设置，则返回currentReservations
        return currentAttendees != null ? currentAttendees : currentReservations; 
    }
    public void setCurrentAttendees(Integer currentAttendees) { this.currentAttendees = currentAttendees; }
} 