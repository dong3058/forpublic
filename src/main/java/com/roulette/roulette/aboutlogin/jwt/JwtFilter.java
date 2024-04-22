package com.roulette.roulette.aboutlogin.jwt;

import com.roulette.roulette.aboutlogin.exceptions.AlReadyLoginError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {




    @Autowired
    private JwtUtill jwtUtill;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String token=resolvetoken((HttpServletRequest) servletRequest);

        try{if(token!=null&&jwtUtill.validatetoken(token)){

            Boolean tokenchec=jwtUtill.validatetoken(token);
            Authentication authentication= jwtUtill.getauth(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        }
        catch(Exception e){

            servletRequest.setAttribute("e",e);
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    public String resolvetoken(HttpServletRequest req){
        String token=req.getHeader("Authorization");
        if(StringUtils.hasText(token)&&token.startsWith("Bearer")&&token.length()>7){
            return token.substring(7);
        }
        return null;
    }
}
