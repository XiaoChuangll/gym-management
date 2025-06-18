package com.gym.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * 教练实体类
 */
@Entity
@Table(name = "coach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "coach_id", length = 36)
    private String coachId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "contact", nullable = false, length = 20)
    private String contact;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
} 