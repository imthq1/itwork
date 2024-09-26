package com.example.demo.controller;

import com.example.demo.domain.Resume;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.resumes.ResCreatedDTO;
import com.example.demo.domain.response.resumes.ResFetchResumeDTO;
import com.example.demo.domain.response.resumes.ResUpdateResumeDTO;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.service.ResumeService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }
    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreatedDTO> addResume(@RequestBody Resume resume) throws IdInvalidException {

        boolean isIdExist=resumeService.checkResumeExistsByUserAndJob(resume);
        if(isIdExist==false){
            throw new IdInvalidException("User id/Jod id khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.handleCreateResume(resume));
    }
    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> reqResumeOptinal= Optional.ofNullable(this.resumeService.findById(resume.getId()));

        if(reqResumeOptinal.isEmpty()){
            throw new IdInvalidException("Resume voi id = "+resume.getId()+" khong ton tai");
        }
        Resume reqResume=reqResumeOptinal.get();
        reqResume.setStatus(resume.getStatus());
return ResponseEntity.ok(this.resumeService.updateResume(reqResume));
    }
    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptinal= Optional.ofNullable(this.resumeService.findById(id));
        if(reqResumeOptinal.isEmpty()){
            throw new IdInvalidException("Resume voi id = "+id+" khong ton tai");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Find a resume by id")
    public ResponseEntity<ResFetchResumeDTO> findById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptinal= Optional.ofNullable(this.resumeService.findById(id));
        if(reqResumeOptinal.isEmpty()){
            throw new IdInvalidException("Resume voi id = "+id+" khong ton tai");
        }
        return ResponseEntity.ok().body(this.resumeService.getRsume(reqResumeOptinal.get()));
    }
    @GetMapping("/resumes")
    @ApiMessage("Get all resume")
    public ResponseEntity<ResultPaginationDTO> findAll(@Filter Specification<Resume> specification, Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok().body(this.resumeService.getAllResumes(specification,pageable));
    }
}
