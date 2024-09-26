//package com.example.demo.util.error;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//
//
//
//@Component
//public class FilterChainExceptionHandler extends OncePerRequestFilter {
//    private final Logger logger= LoggerFactory.getLogger(getClass());
//    @Autowired
//    @Qualifier("handlerExceptionResolver")
//    private HandlerExceptionResolver handlerExceptionResolver;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//    throws ServletException, IOException {
//        try{
//            filterChain.doFilter(request, response);
//        }catch(Exception ex){
//            logger.error("Spring Security Filter Chain Exception: ", ex);
//            handlerExceptionResolver.resolveException(request, response, null, ex);
//        }
//    }
//
//
//}
