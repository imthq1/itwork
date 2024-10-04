package com.example.demo.domain.response.resumes;

import com.example.demo.util.constant.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResFetchResumeDTO {
    private long id;
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String companyName;
    private String createdBy;
    private String updatedBy;
    private UserResume user;
    private JobResume job;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResume{
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobResume{
        private long id;
        private String name;
    }

}
