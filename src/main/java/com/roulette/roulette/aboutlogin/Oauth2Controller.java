package com.roulette.roulette.aboutlogin;

import com.roulette.roulette.entity.Member;
import com.roulette.roulette.aboutlogin.exceptions.AccessTokenRefresh;
import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Optional;
@Controller
@Slf4j
public class Oauth2Controller {
    private RedisTemplate<String,String> redisTemplate;

    private JwtUtill jwtUtill;

    private MemberService memberService;
    @Autowired
    public Oauth2Controller(@Qualifier("redisTemplate") RedisTemplate<String,String> redisTemplate, JwtUtill jwtUtill, MemberService memberService){
        this.redisTemplate=redisTemplate;

        this.jwtUtill=jwtUtill;
        this.memberService=memberService;
    }
    @GetMapping("/kakaologin")
    public void kakaologin(HttpServletResponse resp)throws IOException {
        resp.sendRedirect("/oauth2/authorization/kakao");
    }




    @GetMapping("/googlelogin")
    public void googlelogin(HttpServletResponse resp)throws IOException {
        resp.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/api1")
    @ResponseBody
    public String api1(){

        return "api1";
    }

    @GetMapping("/logouts")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> logout(HttpServletRequest req){
        String access_token=req.getHeader("Authorization").substring(7);
        log.info("로그아웃용 accesstoken:{}",access_token);
        redisTemplate.delete(access_token);
        log.info("--------로그아웃 성공--------------");
        return new ResponseEntity<>(new AccessTokenRefresh(null,"200","/"), HttpStatus.OK);

    }
    @GetMapping("/test/{accesstoken}/{redirecturl}")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> test(@PathVariable(name="accesstoken") String token, @PathVariable("redirecturl") String url){
        log.info("testurl로 성공적인 데이터 전송 성공");
        return new ResponseEntity<>(new AccessTokenRefresh(token,"400",url),HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/testdata")
    @ResponseBody
    public ResponseEntity<Member> dataetest(HttpServletRequest req){
        String access_token=req.getHeader("Authorization");
        Claims claims=jwtUtill.getclaims(access_token.substring(7));
        log.info("claims:{}",claims.getSubject());
        log.info("access+token:{}",access_token);
        Optional<Member> member=memberService.findmemberbyemail((String) claims.getSubject());
        return new ResponseEntity<>(member.get(),HttpStatus.OK);
    }

    @GetMapping("/api2")
    @ResponseBody
    public String api2(){
        return "api2";
    }



    @GetMapping("/login")
    public String login(){

        return "login";
    }
    @GetMapping("/error")
    @ResponseBody
    public String error(){
        return "Error발생";
    }
    @GetMapping("/")
    public String home(HttpServletRequest req)
    {
        log.info("req:{}",req.getHeader("Authorization"));
        return "home";
    }


}
