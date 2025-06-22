package com.gym.management.controller;

import com.gym.management.dto.CoachDTO;
import com.gym.management.dto.CourseDTO;
import com.gym.management.dto.MemberDTO;
import com.gym.management.service.CoachService;
import com.gym.management.service.CourseService;
import com.gym.management.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
     * 根路径访问直接重定向到登录页面
     * 让登录页面处理自动登录的逻辑
     */
    @GetMapping("/")
    public String root(HttpServletRequest request) {
        // 获取可能存在的Cache-Control头
        String cacheControl = request.getHeader("Cache-Control");
        boolean noCache = cacheControl != null && (cacheControl.contains("no-cache") || cacheControl.contains("max-age=0"));
        
        // 从哪个页面链接过来的
        String referer = request.getHeader("Referer");
        boolean isDirectAccess = referer == null || !referer.contains(request.getServerName());
        
        // 总是重定向到登录页面，并添加forceAnimation=true参数
        // 确保即使有记住我cookie，也会显示登录动画
        String animationParam = (isDirectAccess || noCache) ? "forceAnimation=true" : "";
        return "redirect:/login" + (animationParam.isEmpty() ? "" : "?" + animationParam);
    }
    
    /**
     * 显示首页（需要登录）
     */
    @GetMapping("/index")
    public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // 设置缓存控制头，防止浏览器缓存
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 未登录，重定向到登录页面
            return "redirect:/login";
        }
        
        // 设置缓存控制头，防止退出后仍能通过浏览器后退访问
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
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
    public String settings(HttpServletRequest request, HttpServletResponse response) {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // 未登录，重定向到登录页面
            return "redirect:/login";
        }
        
        // 设置缓存控制头，防止退出后仍能通过浏览器后退访问
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        return "settings";
    }
} 