package com.example.demo.controller;

import com.example.demo.domain.Job;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.job.ResCreateJobDTO;
import com.example.demo.domain.response.job.ResUpdateJobDTO;
import com.example.demo.service.JobService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.ok().body(this.jobService.createJobDTO(job));
    }
    @DeleteMapping("/jobs")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@RequestBody Job job) throws IdInvalidException {
        if(this.jobService.findById(job.getId())==null)
        {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.deleteJob(job.getId());
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/jobs")
    @ApiMessage("Get job with pagination")
    public ResultPaginationDTO getJob(@Filter Specification<Job> specification
    , Pageable pageable) {

        return this.jobService.getAllJob(specification,pageable);
    }
    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable Long id) throws IdInvalidException {
        Optional<Job> job = Optional.ofNullable(this.jobService.findById(id));
        if (!job.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(job.get());
    }
    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@RequestBody Job job) throws IdInvalidException {
        Optional<Job> job1 = Optional.ofNullable(this.jobService.findById(job.getId()));
        if(!job1.isPresent())
        {
            throw new IdInvalidException("Job not found");
        }



        return ResponseEntity.ok().body(this.jobService.update(job));
    }
}
