package com.example.demo.domain;

import com.example.demo.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name khong duoc de trong")
    private String name;

    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany
    @JsonIgnoreProperties(value = {"roles"})
    @JoinTable(name = "permission_role",joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

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
