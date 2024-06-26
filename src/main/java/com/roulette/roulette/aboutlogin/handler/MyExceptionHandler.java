package com.roulette.roulette.aboutlogin.handler;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;

@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    private JwtUtill jwtUtill;
    private RedisTemplate<String,String> redisTemplate;



    @Autowired
    public MyExceptionHandler(@Qualifier("redisTemplate") RedisTemplate<String,String> redisTemplate, JwtUtill jwtUtill) {
        this.redisTemplate =redisTemplate;
        this.jwtUtill=jwtUtill;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public void ExprieJwt(HttpServletRequest req, HttpServletResponse resp, ExpiredJwtException e) throws IOException {
        log.info("access token 재발행 필요");
        String accesstokenold=req.getHeader("Authorization").substring(7);

       String refreshtoken=findrefreshtoken(accesstokenold);

        log.info("refreshtoken in exhandler:{}",refreshtoken);

        try{
            if(refreshtoken!=null){

                JwtToken re_gen_token=refillaccesstoken(refreshtoken);

                deloldtoken(accesstokenold);


                resp.sendRedirect("/test/"+re_gen_token.getAccesstoken()+req.getRequestURI());
            }
            else{
                throw new RefreshNullException();
            }

        }
        catch(RefreshNullException exception){
            log.info("리프래시 토큰 재발급 필요---->즉 재로그인 요망");

            resp.sendRedirect("/test/no/login");
        }



    }


    @ExceptionHandler({SecurityException.class, MalformedJwtException.class, UnsupportedJwtException.class, EtcError.class})
    public void authexception(HttpServletRequest req,HttpServletResponse resp,Exception e)throws IOException{

        log.info("기타예외들 발생:{}, 에러발생한 uri:{}",e.getClass(),req.getRequestURI());
        resp.sendRedirect("/test/no/home");

    }

    public String resolvetoken(HttpServletRequest req){
        String token=req.getHeader("Authorization");
        if(StringUtils.hasText(token) &&token.startsWith("Bearer")){
            return token.substring(7);
        }
        return null;
    }


    public JwtToken refillaccesstoken(String refresh_token){
        //Authentication authentication=jwtUtill.getauthforrefresh(token);
        List<Object> datalist=jwtUtill.getdatafromtoken(refresh_token);
        Long id=(Long) datalist.get(0);
        String username=(String) datalist.get(1);

        JwtToken re_gen_token=jwtUtill.genjwt(username,id);
        return re_gen_token;
    }
    public void deloldtoken(String oldtoken){

        redisTemplate.delete(oldtoken);

        //ValueOperations<String,String> operations=redisTemplate.opsForValue();
        //operations.set(re_gen_token.getAccesstoken(),re_gen_token.getRefreshtoken(),60, TimeUnit.SECONDS);
    }

    public String findrefreshtoken(String access_token){
        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        return operations.get(access_token);

    }
}
