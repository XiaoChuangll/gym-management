package com.gym.management.controller;

import com.gym.management.dto.MemberDTO;
import com.gym.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员控制器
 */
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 显示会员列表页面
     */
    @GetMapping
    public String listMembers(Model model) {
        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "member/list";
    }

    /**
     * 显示添加会员页面
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "member/add";
    }

    /**
     * 显示编辑会员页面
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        MemberDTO member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        return "member/edit";
    }

    /**
     * 保存会员
     */
    @PostMapping
    public String saveMember(@ModelAttribute MemberDTO memberDTO) {
        memberService.createMember(memberDTO);
        return "redirect:/members";
    }

    /**
     * 更新会员
     */
    @PostMapping("/{id}")
    public String updateMember(@PathVariable("id") String id, @ModelAttribute MemberDTO memberDTO) {
        memberService.updateMember(id, memberDTO);
        return "redirect:/members";
    }

    /**
     * 删除会员
     */
    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") String id) {
        memberService.deleteMember(id);
        return "redirect:/members";
    }

    /**
     * 根据名称搜索会员
     */
    @GetMapping("/search")
    public String searchMembers(@RequestParam("name") String name, Model model) {
        List<MemberDTO> members = memberService.findMembersByName(name);
        model.addAttribute("members", members);
        return "member/list";
    }

    /**
     * API接口：获取所有会员
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    /**
     * API接口：根据ID获取会员
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable("id") String id) {
        MemberDTO member = memberService.getMemberById(id);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * API接口：创建会员
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO createdMember = memberService.createMember(memberDTO);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    /**
     * API接口：更新会员
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<MemberDTO> updateMemberApi(@PathVariable("id") String id, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    /**
     * API接口：删除会员
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteMemberApi(@PathVariable("id") String id) {
        memberService.deleteMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 