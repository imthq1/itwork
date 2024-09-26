package com.example.demo.domain;

import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.LevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name="jobs")
@Getter
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name không được để trống")
    private String name;
    @NotBlank(message = "location không được để trống")
    private String location;
    private double salary;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createAt;
    private Instant updateAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})
    @JoinTable(name = "job_skill",joinColumns = @JoinColumn(name = "job_id"),
    inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "job",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy= SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():null;
        this.createAt= Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt= Instant.now();
        this.updatedBy= SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():null;
    }
}
