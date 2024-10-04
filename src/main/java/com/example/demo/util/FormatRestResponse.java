package com.example.demo.util;

import com.example.demo.domain.response.RestResponse;
import com.example.demo.util.annontation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpResponse.getStatus();

        if(body instanceof String) {
            return body;
        }
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(status);
        if(status >= 400) {
            //case error
            return body;
        }
        //case success
        else {
            restResponse.setData(body);
            ApiMessage message=returnType.getMethodAnnotation(ApiMessage.class);
            restResponse.setMessage(message!=null ? message.value():"Call API SUCCESS");


        }
        return restResponse;
    }
}