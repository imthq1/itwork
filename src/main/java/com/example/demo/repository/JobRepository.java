package com.example.demo.repository;

import com.example.demo.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Job save(Job job);
    Job deleteById(long id);
    Job findById(long id);
    List<Job> findAll();
}
