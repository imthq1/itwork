package com.example.demo.service;

import com.example.demo.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.util.Collections;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {

    private final UserService userService;
    public UserDetailCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=this.userService.GetUserByUsername(username);
        if (user == null)
        {
            throw new UsernameNotFoundException("username/password error");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
