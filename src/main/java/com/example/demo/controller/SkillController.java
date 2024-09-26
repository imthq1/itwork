package com.example.demo.controller;

import com.example.demo.domain.Skill;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.service.SkillService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @ApiMessage("Create a skill")
    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill skill1 = this.skillService.findByName(skill.getName());
        if (skill1 != null) {
            throw new IdInvalidException("skill này đã tồn tài");
        }
        return ResponseEntity.ok().body(this.skillService.save(skill));
    }


    @ApiMessage("Update a skill")
    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill skill1 = this.skillService.findById(skill.getId());
        if (skill1 != null) {
            throw new IdInvalidException("Id "+ skill.getId()+" không tồn tại");
        }
        if(skill.getName()!=null&&this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name: " + skill.getName() + " đã tồn tại.");
        }
        skill1.setName(skill.getName());

        return ResponseEntity.ok().body(this.skillService.save(skill1));
    }


    @ApiMessage("Get All Skill")
    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(@Filter Specification<Skill> specification,
                                                           Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.findAll(specification, pageable));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) throws IdInvalidException {
        if (this.skillService.findById(id) == null) {
            throw new IdInvalidException("Skill không tồn tại");
        }
        this.skillService.deleteSkillById(id);
        return ResponseEntity.ok().build();
    }



}
