package com.example.demo.domain;

import com.example.demo.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name không được để trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a",timezone = "GTM+7")
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;



    @PrePersist
    public void BeforeCreate(){
        this.createdBy= SecurityUtil.getCurrentUserLogin().isPresent()== true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void BeforeUpdate(){
        this.updatedAt = Instant.now();
        this.updatedBy= SecurityUtil.getCurrentUserLogin().isPresent()==true
                ? SecurityUtil.getCurrentUserLogin().get():"";
    }

}
