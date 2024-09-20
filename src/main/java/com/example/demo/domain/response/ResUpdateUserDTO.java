package com.example.demo.domain.response;

import com.example.demo.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private int id;
    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    private Instant updatedAt;

    @PreUpdate
    public void BeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
