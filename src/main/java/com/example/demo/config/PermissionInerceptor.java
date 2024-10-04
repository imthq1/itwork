package com.example.demo.config;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
//ben User role: lazy
@Transactional
public class PermissionInerceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response
            , Object handler) throws Exception {
        //request cua user
        String path=(String)request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI=request.getRequestURI();
        String httpMethod=request.getMethod();
        System.out.println(">> RUN preHandle");
        System.out.println(">> path:"+path);
        System.out.println(">> httpMethod:"+httpMethod);
        System.out.println(">> requestURI:"+requestURI);

        //check permission
        String email= SecurityUtil.getCurrentUserLogin().isPresent()==true
                ?SecurityUtil.getCurrentUserLogin().get():"";
        if(email!=null &&!email.isEmpty()){
            User currentUser=this.userService.GetUserByUsername(email);
            if(currentUser!=null){
                Role role=currentUser.getRole();
                if(role!=null) {
                    List<Permission> listPermission = role.getPermissions();
                    boolean isAllow = listPermission.stream().anyMatch(
                            iteam -> iteam.getApiPath().equals(path)
                                    && iteam.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new PermissionException("Ban khong co quyen truy cap vao api nay!");
                    }
                }
                else {
                    throw new PermissionException("Ban khong co quyen truy cap vao api nay!");
                }
            }
        }

        return true;
    }
}
