package com.example.demo.service;


import com.example.demo.domain.Skill;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository=skillRepository;
    }
    public Skill findByName(String name) {
        return skillRepository.findByName(name);
    }
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }
    public Skill findById(long id) {
        return skillRepository.findById(id);
    }
    public ResultPaginationDTO findAll(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skills = skillRepository.findAll(spec,pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(skills.getTotalPages());
        meta.setTotal(skills.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(skills.getContent());
    return resultPaginationDTO;
    }
    public void deleteSkillById(long id) {
        Optional<Skill> skill = Optional.ofNullable(skillRepository.findById(id));
        Skill currentSkill = skill.get();

        currentSkill.getJobs().forEach(f->f.getSkills().remove(currentSkill));
        //delete Skill
        currentSkill.getSubscribers().forEach(s->s.getSkills().remove(currentSkill));

        skillRepository.delete(skill.get());
    }
    public boolean existsById(long id) {
        return this.skillRepository.existsById(id);
    }
    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }
}
