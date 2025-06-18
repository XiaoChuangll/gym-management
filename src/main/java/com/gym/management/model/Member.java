package com.gym.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * 会员实体类
 */
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "member_id", length = 36)
    private String memberId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "contact", nullable = false, length = 20)
    private String contact;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
} 