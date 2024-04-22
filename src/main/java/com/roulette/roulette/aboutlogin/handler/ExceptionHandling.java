package com.roulette.roulette.aboutlogin.handler;

import com.roulette.roulette.aboutlogin.exceptions.AlReadyLoginError;
import com.roulette.roulette.aboutlogin.exceptions.EtcError;
import com.roulette.roulette.aboutlogin.exceptions.RefreshNullException;
import com.roulette.roulette.aboutlogin.jwt.JwtToken;
import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
@ControllerAdvice
@Slf4j
public class ExceptionHandling {
    private JwtUtill jwtUtill;
    private RedisTemplate<String,String> redisTemplate;



    @Autowired
    public ExceptionHandling(@Qualifier("redisTemplate") RedisTemplate<String,String> redisTemplate, JwtUtill jwtUtill) {
        this.redisTemplate =redisTemplate;
        this.jwtUtill=jwtUtill;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public void ExprieJwt(HttpServletRequest req, HttpServletResponse resp, ExpiredJwtException e) throws IOException {
        log.info("access token 재발행 필요");
        String accesstokenold=req.getHeader("Authorization").substring(7);

        String refreshtoken=null;
        log.info("refreshtoken in exhandler:{}",refreshtoken);

        try{
            if(refreshtoken!=null){

                JwtToken jwtToken=refillaccesstoken(refreshtoken);

                savenewtoken(accesstokenold,jwtToken);


                resp.setHeader("Authorization", "Bearer "+jwtToken.getAccesstoken());

                resp.sendRedirect("/test/"+jwtToken.getAccesstoken()+req.getRequestURI());
            }
            else{
                throw new RefreshNullException();
            }

        }
        catch(RefreshNullException exception){


            resp.sendRedirect("/test/"+"no/"+"login");
        }



    }
    @ExceptionHandler(AlReadyLoginError.class)
    public void alreadylogin(HttpServletRequest req,HttpServletResponse resp,AlReadyLoginError e)throws IOException{


        resp.sendRedirect("/test/no"+req.getRequestURI());
    }


    @ExceptionHandler({SecurityException.class, MalformedJwtException.class, UnsupportedJwtException.class, EtcError.class})
    public void authexception(HttpServletRequest req,HttpServletResponse resp,Exception e)throws IOException{

        log.info("exceotuibgabdker 작동함!!!!!!!!!:{}",req.getRequestURI());
        resp.sendRedirect("/test/no/home");

    }

    public String resolvetoken(HttpServletRequest req){
        String token=req.getHeader("Authorization");
        if(StringUtils.hasText(token) &&token.startsWith("Bearer")){
            return token.substring(7);
        }
        return null;
    }


    public JwtToken refillaccesstoken(String token){
        Authentication authentication=jwtUtill.getauthforrefresh(token);
        Long id=jwtUtill.getidfromtoken(token);
        JwtToken jwtToken=jwtUtill.genjwt2(authentication.getAuthorities(),(String) authentication.getPrincipal(),id);
        return jwtToken;
    }
    public void savenewtoken(String oldtoken,JwtToken jwtToken){

        redisTemplate.delete(oldtoken);

        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        operations.set(jwtToken.getAccesstoken(),jwtToken.getRefreshtoken(),1000, TimeUnit.SECONDS);
    }
}
