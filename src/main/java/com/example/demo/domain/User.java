package com.example.demo.domain;

import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String name;
    @NotBlank(message = "Email khong duoc de trong")
    private String email;
    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    private int age;

    //Cho DB biet luu duoi dang String
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;



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
