package com.example.demo.repository;

import com.example.demo.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> , JpaSpecificationExecutor<Resume> {
    Resume findById(long id);
    Resume deleteById(long id);
}
