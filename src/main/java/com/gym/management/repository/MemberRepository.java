package com.gym.management.repository;

import com.gym.management.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员数据访问接口
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByNameContaining(String name);
    
    boolean existsByContact(String contact);
} 