package com.gym.management.controller;

import com.gym.management.dto.CourseDTO;
import com.gym.management.dto.MemberDTO;
import com.gym.management.dto.ReservationDTO;
import com.gym.management.service.CourseService;
import com.gym.management.service.MemberService;
import com.gym.management.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约控制器
 */
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final MemberService memberService;
    private final CourseService courseService;

    @Autowired
    public ReservationController(ReservationService reservationService, 
                                MemberService memberService, 
                                CourseService courseService) {
        this.reservationService = reservationService;
        this.memberService = memberService;
        this.courseService = courseService;
    }

    /**
     * 显示预约列表页面
     */
    @GetMapping
    public String listReservations(Model model) {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    /**
     * 显示添加预约页面
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("reservation", new ReservationDTO());
        List<MemberDTO> members = memberService.getAllMembers();
        List<CourseDTO> courses = courseService.getAllCourses();
        model.addAttribute("members", members);
        model.addAttribute("courses", courses);
        return "reservation/add";
    }

    /**
     * 保存预约
     */
    @PostMapping
    public String saveReservation(@ModelAttribute ReservationDTO reservationDTO) {
        reservationService.createReservation(reservationDTO);
        return "redirect:/reservations";
    }

    /**
     * 显示编辑预约状态页面
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        model.addAttribute("reservation", reservation);
        return "reservation/edit";
    }

    /**
     * 更新预约状态
     */
    @PostMapping("/{id}")
    public String updateReservationStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        reservationService.updateReservationStatus(id, status);
        return "redirect:/reservations";
    }

    /**
     * 删除预约
     */
    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable("id") String id) {
        reservationService.deleteReservation(id);
        return "redirect:/reservations";
    }

    /**
     * 根据会员ID查询预约
     */
    @GetMapping("/member/{memberId}")
    public String getReservationsByMemberId(@PathVariable("memberId") String memberId, Model model) {
        List<ReservationDTO> reservations = reservationService.getReservationsByMemberId(memberId);
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    /**
     * 根据课程ID查询预约
     */
    @GetMapping("/course/{courseId}")
    public String getReservationsByCourseId(@PathVariable("courseId") String courseId, Model model) {
        List<ReservationDTO> reservations = reservationService.getReservationsByCourseId(courseId);
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    /**
     * API接口：获取所有预约
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    /**
     * API接口：根据ID获取预约
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable("id") String id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    /**
     * API接口：创建预约
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDTO) {
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    /**
     * API接口：更新预约状态
     */
    @PatchMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ReservationDTO> updateReservationStatus(@PathVariable("id") String id, @RequestBody ReservationDTO reservationDTO) {
        ReservationDTO updatedReservation = reservationService.updateReservationStatus(id, reservationDTO.getStatus());
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    /**
     * API接口：删除预约
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteReservationApi(@PathVariable("id") String id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 