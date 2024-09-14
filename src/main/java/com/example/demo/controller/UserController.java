package com.example.demo.controller;

import com.example.demo.domain.DTO.ResultPaginationDTO;
import com.example.demo.domain.DTO.CreateUserDTO;
import com.example.demo.domain.DTO.UpdateUserDTO;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userRepository=userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiMessage("Create a new user")
    @PostMapping("/users")
    public ResponseEntity<CreateUserDTO> create(@Valid  @RequestBody User user) throws Exception{
        if(this.userRepository.existsByEmail(user.getEmail())){
            throw new IdInvalidException("Email already exists");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        this.userService.save(user);
        CreateUserDTO userDTO=this.userService.createUserDTO(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<User> delete(@PathVariable("id") long id) throws IdInvalidException {
        User user=this.userService.findById(id);
        if (user ==null) {
            throw new IdInvalidException("User not found");
        }

        userService.Delete(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<CreateUserDTO> get(@PathVariable("id") long id) throws IdInvalidException {
        User user = userService.findById(id);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }
        return ResponseEntity.ok(this.userService.createUserDTO(user));
    }

    @ApiMessage("Get All Users")
    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<User> spec,
            Pageable pageable
            )
    {
        return ResponseEntity.ok(this.userService.findAll(spec,pageable));
    }
    @ApiMessage("Update a user")
    @PutMapping("/users")
    public ResponseEntity<UpdateUserDTO> update(@RequestBody User user) throws Exception{
        User user1=this.userService.findById(user.getId());
        if(user1==null){
            throw new IdInvalidException("Id  already exists");
        }
        user1.setId(user.getId());
        user1.setName(user.getName());
        user1.setGender(user.getGender());
        user1.setAge(user.getAge());
        user1.setAddress(user.getAddress());

        this.userService.save(user);
        UpdateUserDTO up=new UpdateUserDTO();
        up.setId(user.getId());
        up.setName(user.getName());
        up.setGender(user.getGender());
        up.setAge(user.getAge());
        up.setAddress(user.getAddress());
        up.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(up);
    }
}