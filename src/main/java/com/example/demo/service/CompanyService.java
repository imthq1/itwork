package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.DTO.ResultPaginationDTO;
import com.example.demo.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    public Company CreateCompany(Company company) {
        return companyRepository.save(company);
    }
    public ResultPaginationDTO findAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        
        meta.setPages(companies.getTotalPages());
        meta.setTotal(companies.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(companies.getContent());
        return rs;
    }
    public Company DeleteById(long id) {
        return this.companyRepository.deleteById(id);
    }
    public Company UpdateCompany(Company company) {
        Company oldCompany =this.companyRepository.findById(company.getId());
        if(oldCompany != null) {
            oldCompany.setName(company.getName());
            oldCompany.setAddress(company.getAddress());
            oldCompany.setDescription(company.getDescription());
            oldCompany.setLogo(company.getLogo());
        }
        return this.CreateCompany(oldCompany);
    }
    public Company findById(long id) {
        return this.companyRepository.findById(id);
    }
}
