package com.example.demo.service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Resume;

import com.example.demo.domain.User;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.resumes.ResCreatedDTO;
import com.example.demo.domain.response.resumes.ResFetchResumeDTO;
import com.example.demo.domain.response.resumes.ResUpdateResumeDTO;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }
    public ResCreatedDTO handleCreateResume(Resume resume)  {
        ResCreatedDTO resCreatedDTO = new ResCreatedDTO();
        resume=this.resumeRepository.save(resume);
        resCreatedDTO.setId(resume.getId());
        resCreatedDTO.setCreatedBy(resume.getCreatedBy());
        resCreatedDTO.setCreatedAt(resume.getCreatedAt());
        return resCreatedDTO;
    }

    public boolean checkResumeExistsByUserAndJob(Resume resume) {
        if(resume.getUser()==null) {
            return false;
        }
        Optional<User> optionalUser = Optional.ofNullable(this.userRepository.findById(resume.getUser().getId()));

        if(optionalUser.isEmpty()||optionalUser==null)
            return false;
        if(resume.getJob()==null) {
            return false;
        }
        Optional<Job> jobOptional=Optional.ofNullable(this.jobRepository.findById(resume.getJob().getId()));
        if(jobOptional.isEmpty()||jobOptional==null)
            return false;
        return true;
    }
    public Resume findById(long id) {
        return this.resumeRepository.findById(id);
    }
    public ResUpdateResumeDTO updateResume(Resume resume) {
        resume=this.resumeRepository.save(resume);
        ResUpdateResumeDTO res=new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        return res;
    }
    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }
    public ResFetchResumeDTO getRsume(Resume resume) {
        ResFetchResumeDTO res=new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        ResFetchResumeDTO.UserResume user=new ResFetchResumeDTO.UserResume(resume.getUser().getId(),resume.getUser().getName());
        res.setUser(user);
        ResFetchResumeDTO.JobResume job=new ResFetchResumeDTO.JobResume(resume.getJob().getId(),resume.getJob().getName());
        res.setJob(job);
        return res;
    }
    public ResultPaginationDTO getAllResumes(Specification<Resume> specification, Pageable pageable)
    {
        Page<Resume> page=this.resumeRepository.findAll(specification,pageable);
        ResultPaginationDTO res=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        res.setMeta(meta);

        List<ResFetchResumeDTO> listResume=page.getContent()
                .stream().map(item->this.getRsume(item))
                .collect(Collectors.toList());
        res.setResult(listResume);

        return res;
    }
}
