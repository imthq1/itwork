package com.example.demo.service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.job.ResCreateJobDTO;
import com.example.demo.domain.response.job.ResUpdateJobDTO;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public ResCreateJobDTO createJobDTO(Job job) {
        //check skills
        if (job.getSkills() != null) {
            List<Long> skills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> skillList = skillRepository.findAllById(skills);
            job.setSkills(skillList);
        }
        Job savedJob = jobRepository.save(job);

        ResCreateJobDTO createJobDTO = new ResCreateJobDTO();
        createJobDTO.setId(savedJob.getId());
        createJobDTO.setName(savedJob.getName());
        createJobDTO.setLocation(savedJob.getLocation());
        createJobDTO.setSalary(savedJob.getSalary());
        createJobDTO.setQuantity(savedJob.getQuantity());
        createJobDTO.setLevel(savedJob.getLevel());
        createJobDTO.setStartDate(savedJob.getStartDate());
        createJobDTO.setEndDate(savedJob.getEndDate());
        createJobDTO.setCreateAt(savedJob.getCreateAt());
        createJobDTO.setCreatedBy(savedJob.getCreatedBy());
        //lambda
        if (savedJob.getSkills() != null) {
            List<String> skills = savedJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            createJobDTO.setSkill(skills);
        }
//    if(savedJob.getSkills()!=null){
//        List<String> list=new ArrayList<>();
//        for(Skill skills: savedJob.getSkills())
//        {
//    list.add(skills.getName());
//        }
//        createJobDTO.setSkill(list);
//    }


        return createJobDTO;
    }

    public Job deleteJob(long id) {
        return this.jobRepository.deleteById(id);
    }

    public Job findById(long id) {
        System.out.println();
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJob(Specification<Job> specification, Pageable pageable) {
        Page<Job> jobs = jobRepository.findAll(specification, pageable);

        ResultPaginationDTO paginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotal(jobs.getTotalElements());
        meta.setPages(jobs.getTotalPages());

        paginationDTO.setMeta(meta);
        paginationDTO.setResult(jobs.getContent());
        return paginationDTO;

    }

    public ResUpdateJobDTO update(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream()
                    .map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> ListSkill = skillRepository.findByIdIn(reqSkills);
            job.setSkills(ListSkill);
        }
        Job savedJob = jobRepository.save(job);
        ResUpdateJobDTO updateJobDTO = new ResUpdateJobDTO();
        updateJobDTO.setId(savedJob.getId());
        updateJobDTO.setName(savedJob.getName());
        updateJobDTO.setLocation(savedJob.getLocation());
        updateJobDTO.setSalary(savedJob.getSalary());
        updateJobDTO.setQuantity(savedJob.getQuantity());
        updateJobDTO.setLevel(savedJob.getLevel());
        updateJobDTO.setStartDate(savedJob.getStartDate());
        updateJobDTO.setEndDate(savedJob.getEndDate());
        updateJobDTO.setCreateAt(savedJob.getCreateAt());
        updateJobDTO.setCreatedBy(savedJob.getCreatedBy());
        if (savedJob.getSkills() != null) {
            List<String> listNamSkill = savedJob.getSkills()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            updateJobDTO.setSkill(listNamSkill);
        }
        return updateJobDTO;
    }
}
