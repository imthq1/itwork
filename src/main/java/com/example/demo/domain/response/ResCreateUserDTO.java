package com.example.demo.domain.response;

import com.example.demo.domain.Company;
import com.example.demo.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;
    private Instant createdAt;

    private Company company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Company{
        private long id;
        private String name;
    }


}
