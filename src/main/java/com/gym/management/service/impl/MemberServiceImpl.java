package com.gym.management.service.impl;

import com.gym.management.dto.MemberDTO;
import com.gym.management.exception.ResourceNotFoundException;
import com.gym.management.model.Member;
import com.gym.management.repository.MemberRepository;
import com.gym.management.service.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员服务实现类
 */
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        Member member = new Member();
        BeanUtils.copyProperties(memberDTO, member);
        member = memberRepository.save(member);
        BeanUtils.copyProperties(member, memberDTO);
        return memberDTO;
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO getMemberById(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("会员不存在，ID: " + memberId));
        return convertToDTO(member);
    }

    @Override
    public MemberDTO updateMember(String memberId, MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("会员不存在，ID: " + memberId));
        
        member.setName(memberDTO.getName());
        member.setContact(memberDTO.getContact());
        member.setJoinDate(memberDTO.getJoinDate());
        member.setMemberType(memberDTO.getMemberType());
        
        member = memberRepository.save(member);
        return convertToDTO(member);
    }

    @Override
    public void deleteMember(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("会员不存在，ID: " + memberId);
        }
        memberRepository.deleteById(memberId);
    }

    @Override
    public List<MemberDTO> findMembersByName(String name) {
        return memberRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     * @param member 会员实体
     * @return 会员DTO
     */
    private MemberDTO convertToDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();
        BeanUtils.copyProperties(member, memberDTO);
        return memberDTO;
    }
} 