package com.example.demo.domain.response.job;

import com.example.demo.util.constant.LevelEnum;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
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

}
