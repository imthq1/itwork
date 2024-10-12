package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.request.ReqLoginDTO;
import com.example.demo.domain.response.ResCreateUserDTO;
import com.example.demo.domain.response.ResUpdateUserDTO;

import com.example.demo.domain.response.ResUserDTO;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.UserRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }
    public User save(User user) {
        return this.userRepository.save(user);
    }
    public void Delete(long id) {
         this.userRepository.deleteById(id);
    }
    public User findById(long id) {
        return this.userRepository.findById(id);
    }

    public ResultPaginationDTO findAll(Specification<User> spec
    , Pageable pageable) {
        Page<User> pageUsers = this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt=new ResultPaginationDTO.Meta();


        //FE gui len
        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUsers.getTotalPages());
        mt.setTotal(pageUsers.getTotalElements());

        rs.setMeta(mt);

//        ResUserDTO.Company company=new ResUserDTO.Company();
//        company.setId();

        List<ResCreateUserDTO> listUser=pageUsers.getContent()
                .stream().map(item->this.createUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User DTOtoUser(ReqLoginDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }
    public User GetUserByUsername(String username) {
        return this.userRepository.getUserByEmail(username);
    }

    public ResCreateUserDTO createUserDTO(User user) {
        ResCreateUserDTO userDTO = new ResCreateUserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setCreatedAt(user.getCreatedAt());

        if(user.getCompany() != null) {
            ResCreateUserDTO.Company company = new ResCreateUserDTO.Company(user.getCompany().getId(),
                    user.getCompany().getName());
            userDTO.setCompany(company);
        }
        if(user.getRole() != null) {
            ResCreateUserDTO.Role role=new ResCreateUserDTO.Role(user.getRole().getId(),
                    user.getRole().getName());
        userDTO.setRole(role);
        }
        return userDTO;
    }
    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            // Lấy ID của công ty từ đối tượng Company và truyền vào phương thức findById
            Optional<Company> companyOptional = Optional.ofNullable(this.companyService.findById(user.getCompany().getId()));
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() :null);
        }
        if(user.getRole()!=null) {
            Role r=this.roleService.findById(user.getRole().getId());
            user.setRole(r!=null?r:null);
        }
        return this.userRepository.save(user);
    }

    public User updateUserDTO(User user) {
        User currentUser=this.findById(user.getId());
        if(currentUser!=null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());
            currentUser.setName(user.getName());
        }
        if(user.getCompany()!=null) {
            Optional<Company> companyOptional = Optional.ofNullable(this.companyService.findById(user.getCompany().getId()));
            currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() :null);
        }

        if(user.getRole()!=null) {
            Role r=this.roleService.findById(user.getRole().getId());
            currentUser.setRole(r!=null?r:null);
        }
        currentUser=this.userRepository.save(currentUser);
        return currentUser;
    }
    public void updateUserToken(String token,String email)
    {
        User currentUser = this.GetUserByUsername(email);
        if(currentUser!=null)
        {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }
    public User getUserByRefreshTokenAndEmail(String refreshToken,String email)
    {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public ResUpdateUserDTO convertUserToDTO(User user) {
        ResUpdateUserDTO userDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.Company company = new ResUpdateUserDTO.Company();
        if(user.getCompany()!=null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            userDTO.setCompany(company);
        }
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setUpdatedAt(user.getUpdatedAt());

        return userDTO;
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
