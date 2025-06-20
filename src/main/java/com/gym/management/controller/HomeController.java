package com.gym.management.controller;

import com.gym.management.dto.CoachDTO;
import com.gym.management.dto.CourseDTO;
import com.gym.management.dto.MemberDTO;
import com.gym.management.service.CoachService;
import com.gym.management.service.CourseService;
import com.gym.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    private final MemberService memberService;
    private final CoachService coachService;
    private final CourseService courseService;

    @Autowired
    public HomeController(MemberService memberService, 
                         CoachService coachService, 
                         CourseService courseService) {
        this.memberService = memberService;
        this.coachService = coachService;
        this.courseService = courseService;
    }

    /**
     * 显示首页
     */
    @GetMapping("/")
    public String home(Model model) {
        // 获取会员总数
        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("memberCount", members.size());
        
        // 获取教练总数
        List<CoachDTO> coaches = coachService.getAllCoaches();
        model.addAttribute("coachCount", coaches.size());
        
        // 获取课程总数
        List<CourseDTO> courses = courseService.getAllCourses();
        model.addAttribute("courseCount", courses.size());
        
        // 获取最新课程
        if (!courses.isEmpty()) {
            model.addAttribute("latestCourses", courses.subList(0, Math.min(5, courses.size())));
        }
        
        return "index";
    }
    
    /**
     * 显示关于页面
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    /**
     * 显示设置页面
     */
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
} 