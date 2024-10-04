package com.example.demo.controller;

import com.example.demo.domain.Company;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.service.CompanyService;
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
public class CompanyController {
    private final CompanyService companyService;
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> addCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok().body(this.companyService.CreateCompany(company));
    }
    @ApiMessage("Get All Companies")
    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.companyService.findAllCompanies(spec,pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok().body(this.companyService.UpdateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@Valid @PathVariable long id) throws IdInvalidException {
        Company company=this.companyService.findById(id);
        if(company==null) {
            throw new IdInvalidException("Company not found");
        }
        this.companyService.DeleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("find company by id")
    public ResponseEntity<Company> getCompanyById(@PathVariable long id) {
        Optional<Company> company=Optional.ofNullable(this.companyService.findById(id));

        return ResponseEntity.ok().body(company.get());
    }
}
