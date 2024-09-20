package com.example.demo.controller;

import com.example.demo.domain.request.ResLoginDTO;
import com.example.demo.domain.request.ReqLoginDTO;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    @Value("${imthang.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, SecurityUtil securityUtil) {
        this.userService=userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
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
                    ,currentUserDB.getName());

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken=this.securityUtil.createAccessToken(authentication.getName(),resLoginDTO.getUserLogin());

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
    throws IdInvalidException{
        ResLoginDTO resLoginDTO=new ResLoginDTO();
        //check refresh_token
        Jwt decodedToken=this.securityUtil.checkValidRefreshToken(refresh_token);
        String email=decodedToken.getSubject();

        User current =this.userService.getUserByRefreshTokenAndEmail(refresh_token,email);
        if(current==null)
        {
            throw new IdInvalidException("Refresh Token khong hop le");
        }

        if(current!=null)
        {
            ResLoginDTO.UserLogin userLogin=new ResLoginDTO.UserLogin(
                    current.getId()
                    ,current.getEmail()
                    ,current.getName());

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken=this.securityUtil.createAccessToken(email,resLoginDTO.getUserLogin());

        resLoginDTO.setAccessToken(AccessToken);

        //create refresh token
        String new_refresh_token=this.securityUtil.createRefreshToken(email,resLoginDTO);
        //update user
        this.userService.updateUserToken(new_refresh_token,email);

        //set cookies
        ResponseCookie resCookies=ResponseCookie.from("refresh_token1",new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().
                header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }
}
