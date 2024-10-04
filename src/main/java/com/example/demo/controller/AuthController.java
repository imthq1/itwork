package com.example.demo.controller;

import com.example.demo.domain.response.ResCreateUserDTO;
import com.example.demo.domain.response.ResLoginDTO;
import com.example.demo.domain.request.ReqLoginDTO;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${imthang.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, SecurityUtil securityUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService=userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO user)
    {
        User user1=this.userService.DTOtoUser(user);
        //nap input vao security
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user1.getEmail(), user1.getPassword());

        //xac thuc

        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(token);

        //set info ng dung dang nhap vao context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO=new ResLoginDTO();

        User currentUserDB=this.userService.GetUserByUsername(user1.getEmail());
        if(currentUserDB!=null)
        {
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(
                    currentUserDB.getId()
                    ,currentUserDB.getEmail()
                    ,currentUserDB.getName()
            ,currentUserDB.getRole());

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken=this.securityUtil.createAccessToken(authentication.getName(),resLoginDTO);

        resLoginDTO.setAccessToken(AccessToken);

        //create refresh token
        String refresh_token=this.securityUtil.createRefreshToken(user1.getEmail(),resLoginDTO);
        //update user
        this.userService.updateUserToken(refresh_token,user1.getEmail());

        //set cookies
        ResponseCookie resCookies=ResponseCookie.from("refresh_token1",refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().
            header(HttpHeaders.SET_COOKIE, resCookies.toString())
                    .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){
    String email=SecurityUtil.getCurrentUserLogin().isPresent()?
            SecurityUtil.getCurrentUserLogin().get():"";

            User user=this.userService.GetUserByUsername(email);
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin();
            ResLoginDTO.UserGetAccount userGetAccount=new ResLoginDTO.UserGetAccount();
            if (user !=null)
            {
                userLogin.setId(user.getId());
                userLogin.setEmail(user.getEmail());
                userLogin.setName(user.getName());
                userLogin.setRole(user.getRole());
                userGetAccount.setUser(userLogin);
            }
    return ResponseEntity.ok().body(userGetAccount);
    }
    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout()throws IdInvalidException{
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():"";
        if(email.equals(""))
        {
            throw new IdInvalidException("Access token khong hop le");
        }

        this.userService.updateUserToken(null,email);
        //remove refresh token cookie
        ResponseCookie resCookies=ResponseCookie.from("refresh_token1",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,resCookies.toString()).body(null);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh toklen")
    public ResponseEntity<ResLoginDTO> getRefreshToken( @CookieValue(name = "refresh_token1",defaultValue = "ABC") String refresh_token)
    throws IdInvalidException {
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        //check refresh_token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        User current = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (current == null) {
            throw new IdInvalidException("Refresh Token khong hop le");
        }

        if (current != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    current.getId()
                    , current.getEmail()
                    , current.getName()
                    , current.getRole())
                    ;

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken = this.securityUtil.createAccessToken(email, resLoginDTO);

        resLoginDTO.setAccessToken(AccessToken);

        //create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, resLoginDTO);
        //update user
        this.userService.updateUserToken(new_refresh_token, email);

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token1", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().
                header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(resLoginDTO);
    }
    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> regiser(@Valid @RequestBody User postManUser) throws IdInvalidException {
        boolean isEmailExist=this.userRepository.existsByEmail(postManUser.getEmail());
        if(isEmailExist==true)
        {
            throw new IdInvalidException("Email "+postManUser.getEmail()+"da ton tai, vui long su dung email khac!");
        }
        String hashPassword=this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User user=this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUserDTO(user));
    }
    }