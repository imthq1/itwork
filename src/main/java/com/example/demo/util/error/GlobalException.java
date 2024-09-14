package com.example.demo.util.error;

import com.example.demo.domain.RestResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value={
            UsernameNotFoundException.class,
            BadRequestException.class,
            IdInvalidException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(ex.getMessage());
        restResponse.setMessage("Exception occurs...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        RestResponse<Object> res=new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors=new ArrayList<>();
        for(FieldError fieldError:fieldErrors){
            errors.add(fieldError.getDefaultMessage());
        }
//        List<String> Errrors=new ArrayList<String>();

//        List<String> errors=fieldErrors.stream().map(f->f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

    }
    //Exception URL
    @ExceptionHandler(value = {
            NoResourceFoundException.class,
    })
    public ResponseEntity<RestResponse<Object>> noResourceFoundException(Exception ex) {
        RestResponse<Object> res=new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("404 Not Foun. URL may not exist...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
