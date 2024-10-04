package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.job.ResCreateJobDTO;
import com.example.demo.domain.response.job.ResUpdateJobDTO;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
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
        if(job.getCompany() != null) {
            Optional<Company> companyOptional=Optional.ofNullable(companyRepository.findById(job.getCompany().getId()));
            if(companyOptional.isPresent()) {
                job.setCompany(companyOptional.get());
            }
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
        createJobDTO.setCreateAt(savedJob.getCreatedAt());
        createJobDTO.setCreatedBy(savedJob.getCreatedBy());
        //lambda
        if (savedJob.getSkills() != null) {
            List<String> skills = savedJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            createJobDTO.setSkill(skills);
        }



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

    public ResUpdateJobDTO update(Job job,Job jobDB) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream()
                    .map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> ListSkill = skillRepository.findByIdIn(reqSkills);
            jobDB.setSkills(ListSkill);
        }

        if(job.getCompany()!=null)
        {
            Optional<Company> companyOptinal=Optional.ofNullable(this.companyRepository.findById(job.getCompany().getId()));
            if(companyOptinal.isPresent())
            {
                jobDB.setCompany(companyOptinal.get());
            }
        }
        jobDB.setName(job.getName());
        jobDB.setSalary(job.getSalary());
        jobDB.setQuantity(job.getQuantity());
        jobDB.setLevel(job.getLevel());
        jobDB.setStartDate(job.getStartDate());
        jobDB.setEndDate(job.getEndDate());
        jobDB.setLocation(job.getLocation());
        jobDB.setActive(job.isActive());
        Job currentJob = jobRepository.save(jobDB);

        ResUpdateJobDTO updateJobDTO = new ResUpdateJobDTO();

        updateJobDTO.setId(currentJob.getId());
        updateJobDTO.setName(currentJob.getName());
        updateJobDTO.setLocation(currentJob.getLocation());
        updateJobDTO.setSalary(currentJob.getSalary());
        updateJobDTO.setQuantity(currentJob.getQuantity());
        updateJobDTO.setLevel(currentJob.getLevel());
        updateJobDTO.setStartDate(currentJob.getStartDate());
        updateJobDTO.setEndDate(currentJob.getEndDate());


        return updateJobDTO;
    }
}
