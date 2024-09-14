package com.example.demo.domain.DTO;

import com.example.demo.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UpdateUserDTO {
    private int id;
    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    private Instant updatedAt;

}
