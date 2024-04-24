package com.roulette.roulette.aboutlogin.handler;

import com.roulette.roulette.aboutlogin.exceptions.EtcError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
@Slf4j
@Component
public class EntryPointHandler implements AuthenticationEntryPoint {
    private HandlerExceptionResolver resolver;
    //private ExceptionHandling exceptionHandling;

    @Autowired
    public EntryPointHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver/*,ExceptionHandling exceptionHandling*/) {
        this.resolver = resolver;
       // this.exceptionHandling=exceptionHandling;
    }



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        if(null!=request.getAttribute("e")) {
            resolver.resolveException(request, response, null, (Exception) request.getAttribute("e"));
        }
        else{
            resolver.resolveException(request, response, null, (Exception) new EtcError());
        }

    }
}
