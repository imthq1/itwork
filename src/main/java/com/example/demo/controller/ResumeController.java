package com.example.demo.controller;

import com.example.demo.domain.Company;
import com.example.demo.domain.Job;
import com.example.demo.domain.Resume;
import com.example.demo.domain.User;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.resumes.ResCreatedDTO;
import com.example.demo.domain.response.resumes.ResFetchResumeDTO;
import com.example.demo.domain.response.resumes.ResUpdateResumeDTO;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.service.ResumeService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService, FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
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
        List<Long> arrJobId=null;
        String email=SecurityUtil.getCurrentUserLogin().isPresent()==true
                ?SecurityUtil.getCurrentUserLogin().get():"";
        User currentUser=this.userService.GetUserByUsername(email);
        if(currentUser!=null){
            Company userCompany=currentUser.getCompany();
            if(userCompany!=null)
            {
                List<Job> companyJobs=userCompany.getJobs();

                if(companyJobs!=null&& companyJobs.size()>0){
                    arrJobId=companyJobs.stream().map(x->x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec=filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobId)).get());
        Specification<Resume> finalSpec=jobInSpec.and(specification);

        return ResponseEntity.ok().body(this.resumeService.getAllResumes(finalSpec,pageable));

    }
    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser( Pageable pageable) throws IdInvalidException {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
