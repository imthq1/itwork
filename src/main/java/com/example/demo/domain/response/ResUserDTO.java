package com.example.demo.domain.response;

import com.example.demo.domain.Company;
import com.example.demo.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updateAt;
    private Instant createAt;
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
