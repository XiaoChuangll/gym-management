package com.gym.management.controller;

import com.gym.management.dto.CoachDTO;
import com.gym.management.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练控制器
 */
@Controller
@RequestMapping("/coaches")
public class CoachController {

    private final CoachService coachService;

    @Autowired
    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    /**
     * 显示教练列表页面
     */
    @GetMapping
    public String listCoaches(Model model) {
        List<CoachDTO> coaches = coachService.getAllCoaches();
        model.addAttribute("coaches", coaches);
        return "coach/list";
    }

    /**
     * 显示添加教练页面
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("coach", new CoachDTO());
        return "coach/add";
    }

    /**
     * 显示编辑教练页面
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        CoachDTO coach = coachService.getCoachById(id);
        model.addAttribute("coach", coach);
        return "coach/edit";
    }

    /**
     * 保存教练
     */
    @PostMapping
    public String saveCoach(@ModelAttribute CoachDTO coachDTO) {
        coachService.createCoach(coachDTO);
        return "redirect:/coaches";
    }

    /**
     * 更新教练
     */
    @PostMapping("/{id}")
    public String updateCoach(@PathVariable("id") String id, @ModelAttribute CoachDTO coachDTO) {
        coachService.updateCoach(id, coachDTO);
        return "redirect:/coaches";
    }

    /**
     * 删除教练
     */
    @GetMapping("/delete/{id}")
    public String deleteCoach(@PathVariable("id") String id) {
        coachService.deleteCoach(id);
        return "redirect:/coaches";
    }

    /**
     * 根据名称搜索教练
     */
    @GetMapping("/search")
    public String searchCoaches(@RequestParam("name") String name, Model model) {
        List<CoachDTO> coaches = coachService.findCoachesByName(name);
        model.addAttribute("coaches", coaches);
        return "coach/list";
    }

    /**
     * 根据专长搜索教练
     */
    @GetMapping("/search/specialty")
    public String searchCoachesBySpecialty(@RequestParam("specialty") String specialty, Model model) {
        List<CoachDTO> coaches = coachService.findCoachesBySpecialty(specialty);
        model.addAttribute("coaches", coaches);
        return "coach/list";
    }

    /**
     * API接口：获取所有教练
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<CoachDTO>> getAllCoaches() {
        List<CoachDTO> coaches = coachService.getAllCoaches();
        return new ResponseEntity<>(coaches, HttpStatus.OK);
    }

    /**
     * API接口：根据ID获取教练
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CoachDTO> getCoachById(@PathVariable("id") String id) {
        CoachDTO coach = coachService.getCoachById(id);
        return new ResponseEntity<>(coach, HttpStatus.OK);
    }

    /**
     * API接口：创建教练
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<CoachDTO> createCoach(@RequestBody CoachDTO coachDTO) {
        CoachDTO createdCoach = coachService.createCoach(coachDTO);
        return new ResponseEntity<>(createdCoach, HttpStatus.CREATED);
    }

    /**
     * API接口：更新教练
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CoachDTO> updateCoachApi(@PathVariable("id") String id, @RequestBody CoachDTO coachDTO) {
        CoachDTO updatedCoach = coachService.updateCoach(id, coachDTO);
        return new ResponseEntity<>(updatedCoach, HttpStatus.OK);
    }

    /**
     * API接口：删除教练
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCoachApi(@PathVariable("id") String id) {
        coachService.deleteCoach(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 