package com.example.demo.domain.response;

import com.example.demo.domain.Company;
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
<<<<<<< HEAD
    private int id;
=======
    private long id;
>>>>>>> master
    private String name;
    private int age;
    private Company company;

    @Getter
    @Setter
    public static class Company{
        private long id;
        private String name;

    }
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    private Instant updatedAt;

    @PreUpdate
    public void BeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
