package com.example.demo.domain.response.job;

import com.example.demo.domain.Skill;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.LevelEnum;
import jakarta.persistence.Column;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
public class ResCreateJobDTO {
    private long id;

    private String name;
    private String location;
    private double salary;
    private int quantity;
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
    private List<String> skill;


    @Getter
    @Setter
    public static class Skill {
        private String name;
    }
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
