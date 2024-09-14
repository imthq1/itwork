package com.example.demo.controller;

import com.example.demo.domain.DTO.ResLoginDTO;
import com.example.demo.domain.DTO.LoginUserDTO;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, SecurityUtil securityUtil) {
        this.userService=userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }
    @RequestMapping("/api/v1")
    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginUserDTO user)
    {
        User user1=this.userService.DTOtoUser(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user1.getEmail(), user1.getPassword());

        //xac thuc

        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(token);

        //create a token => can viet ham loadUserByUsername
        String AccessToken=this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        ResLoginDTO resLoginDTO=new ResLoginDTO();
        resLoginDTO.setAccessToken(AccessToken);
        return ResponseEntity.ok().body(resLoginDTO);
    }
}
