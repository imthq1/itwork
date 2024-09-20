package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.User;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
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
    public void DeleteById(long id) {
        Optional<Company> company = Optional.ofNullable(companyRepository.findById(id));
        if (company.isPresent()) {
            Company company1 = company.get();
            List<User> users=this.userRepository.findByCompany(company1);
            this.userRepository.deleteAll(users);
        }

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
