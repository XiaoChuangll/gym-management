package com.gym.management.service;

import com.gym.management.dto.MemberDTO;

import java.util.List;

/**
 * 会员服务接口
 */
public interface MemberService {
    /**
     * 创建会员
     * @param memberDTO 会员DTO
     * @return 创建的会员DTO
     */
    MemberDTO createMember(MemberDTO memberDTO);
    
    /**
     * 获取所有会员
     * @return 会员DTO列表
     */
    List<MemberDTO> getAllMembers();
    
    /**
     * 根据ID获取会员
     * @param memberId 会员ID
     * @return 会员DTO
     */
    MemberDTO getMemberById(String memberId);
    
    /**
     * 更新会员信息
     * @param memberId 会员ID
     * @param memberDTO 会员DTO
     * @return 更新后的会员DTO
     */
    MemberDTO updateMember(String memberId, MemberDTO memberDTO);
    
    /**
     * 删除会员
     * @param memberId 会员ID
     */
    void deleteMember(String memberId);
    
    /**
     * 根据姓名查询会员
     * @param name 会员姓名
     * @return 会员DTO列表
     */
    List<MemberDTO> findMembersByName(String name);
} 