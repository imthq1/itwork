package com.example.demo.domain.response.resumes;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreatedDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
