package com.gym.management.controller;

import com.gym.management.dto.CoachWorkloadDTO;
import com.gym.management.dto.CourseAttendanceDTO;
import com.gym.management.dto.MemberDTO;
import com.gym.management.dto.MemberReservationDTO;
import com.gym.management.service.MemberService;
import com.gym.management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表控制器
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final MemberService memberService;

    @Autowired
    public ReportController(ReportService reportService, MemberService memberService) {
        this.reportService = reportService;
        this.memberService = memberService;
    }

    /**
     * 显示会员预约情况选择页面
     */
    @GetMapping("/member-reservations-selection")
    public String getMemberReservationsSelection(Model model) {
        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "report/member-reservations-selection";
    }

    /**
     * 在会员预约情况选择页面中搜索会员
     */
    @GetMapping("/member-reservations-selection/search")
    public String searchMembersForReservation(@RequestParam("name") String name, Model model) {
        List<MemberDTO> members = memberService.findMembersByName(name);
        model.addAttribute("members", members);
        model.addAttribute("searchName", name);
        return "report/member-reservations-selection";
    }

    /**
     * 显示会员预约情况页面
     */
    @GetMapping("/member-reservations/{memberId}")
    public String getMemberReservations(@PathVariable("memberId") String memberId, Model model) {
        List<MemberReservationDTO> memberReservations = reportService.getMemberReservations(memberId);
        model.addAttribute("memberReservations", memberReservations);
        model.addAttribute("memberId", memberId);
        return "report/member-reservations";
    }

    /**
     * 显示会员预约情况页面
     */
    @GetMapping("/member-reservationss/{memberId}")
    public String getMemberReservationss(@PathVariable("memberId") String memberId, Model model) {
        List<MemberReservationDTO> memberReservations = reportService.getMemberReservations(memberId);
        model.addAttribute("memberReservations", memberReservations);
        model.addAttribute("memberId", memberId);
        return "report/member-reservationss";
    }

    /**
     * 显示课程出勤率页面
     */
    @GetMapping("/course-attendance")
    public String getCourseAttendance(Model model) {
        List<CourseAttendanceDTO> courseAttendance = reportService.getCourseAttendance();
        model.addAttribute("courseAttendance", courseAttendance);
        return "report/course-attendance";
    }

    /**
     * 显示教练工作量页面
     */
    @GetMapping("/coach-workload")
    public String getCoachWorkload(Model model) {
        List<CoachWorkloadDTO> coachWorkload = reportService.getCoachWorkload();
        model.addAttribute("coachWorkload", coachWorkload);
        return "report/coach-workload";
    }

    /**
     * API接口：获取会员预约情况
     */
    @GetMapping("/api/member-reservations/{memberId}")
    @ResponseBody
    public ResponseEntity<List<MemberReservationDTO>> getMemberReservationsApi(@PathVariable("memberId") String memberId) {
        List<MemberReservationDTO> memberReservations = reportService.getMemberReservations(memberId);
        return new ResponseEntity<>(memberReservations, HttpStatus.OK);
    }

    /**
     * API接口：获取课程出勤率
     */
    @GetMapping("/api/course-attendance")
    @ResponseBody
    public ResponseEntity<List<CourseAttendanceDTO>> getCourseAttendanceApi() {
        List<CourseAttendanceDTO> courseAttendance = reportService.getCourseAttendance();
        return new ResponseEntity<>(courseAttendance, HttpStatus.OK);
    }

    /**
     * API接口：获取教练工作量
     */
    @GetMapping("/api/coach-workload")
    @ResponseBody
    public ResponseEntity<List<CoachWorkloadDTO>> getCoachWorkloadApi() {
        List<CoachWorkloadDTO> coachWorkload = reportService.getCoachWorkload();
        return new ResponseEntity<>(coachWorkload, HttpStatus.OK);
    }
} 