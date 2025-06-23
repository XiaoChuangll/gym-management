package com.gym.management.controller;

import com.gym.management.dto.CoachDTO;
import com.gym.management.dto.CourseDTO;
import com.gym.management.service.CoachService;
import com.gym.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程控制器
 */
@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CoachService coachService;

    @Autowired
    public CourseController(CourseService courseService, CoachService coachService) {
        this.courseService = courseService;
        this.coachService = coachService;
    }

    /**
     * 显示课程列表页面
     */
    @GetMapping
    public String listCourses(Model model) {
        List<CourseDTO> courses = courseService.getAllCourses();
        // 日志输出每个课程的percent和class类型
        for (CourseDTO c : courses) {
            System.out.println("Controller: " + c.getCourseName() + " percent=" + c.getPercent());
        }
        model.addAttribute("courses", courses);
        return "course/list";
    }

    /**
     * 显示添加课程页面
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        List<CoachDTO> coaches = coachService.getAllCoaches();
        model.addAttribute("coaches", coaches);
        return "course/add";
    }

    /**
     * 显示编辑课程页面
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        CourseDTO course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        List<CoachDTO> coaches = coachService.getAllCoaches();
        model.addAttribute("coaches", coaches);
        return "course/edit";
    }

    /**
     * 保存课程
     */
    @PostMapping
    public String saveCourse(@ModelAttribute CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
        return "redirect:/courses";
    }

    /**
     * 更新课程
     */
    @PostMapping("/{id}")
    public String updateCourse(@PathVariable("id") String id, @ModelAttribute CourseDTO courseDTO) {
        courseService.updateCourse(id, courseDTO);
        return "redirect:/courses";
    }

    /**
     * 删除课程
     */
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") String id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }

    /**
     * 根据名称搜索课程
     */
    @GetMapping("/search")
    public String searchCourses(@RequestParam("name") String name, Model model) {
        List<CourseDTO> courses = courseService.findCoursesByName(name);
        model.addAttribute("courses", courses);
        return "course/list";
    }

    /**
     * 根据教练ID查询课程
     */
    @GetMapping("/coach/{coachId}")
    public String getCoursesByCoachId(@PathVariable("coachId") String coachId, Model model) {
        List<CourseDTO> courses = courseService.getCoursesByCoachId(coachId);
        model.addAttribute("courses", courses);
        return "course/list";
    }

    /**
     * 根据时间范围查询课程
     */
    @GetMapping("/search/time")
    public String searchCoursesByTimeRange(
            @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
            Model model) {
        List<CourseDTO> courses = courseService.findCoursesByTimeRange(startTime, endTime);
        model.addAttribute("courses", courses);
        return "course/list";
    }

    /**
     * API接口：获取所有课程
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    /**
     * API接口：根据ID获取课程
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable("id") String id) {
        CourseDTO course = courseService.getCourseById(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    /**
     * API接口：创建课程
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * API接口：更新课程
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CourseDTO> updateCourseApi(@PathVariable("id") String id, @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    /**
     * API接口：删除课程
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCourseApi(@PathVariable("id") String id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 