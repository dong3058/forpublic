package com.roulette.roulette.aboutlogin.handler;

import com.roulette.roulette.entity.Member;
import com.roulette.roulette.aboutlogin.domaindto.MemberDto;
import com.roulette.roulette.aboutlogin.jwt.JwtToken;
import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.service.MemberService;
import com.roulette.roulette.aboutlogin.userinfo.Oauth2userprincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
@Component
@Slf4j
public class SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtill jwtUtill;
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private MemberService memberService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Oauth2userprincipal oauth2userprincipal=(Oauth2userprincipal) authentication.getPrincipal();

        Optional<Member> member=memberService.findmemberbyemail(oauth2userprincipal.getUsername());

        gettokenandresponse(oauth2userprincipal,member,response);



    }

    public void gettokenandresponse(Oauth2userprincipal oauth2userprincipal,Optional<Member> member,HttpServletResponse resp) throws IOException{
       /* if(member.isPresent()){
            Member m=member.get();
            JwtToken jwtToken=jwtUtill.genjwt(oauth2userprincipal.getAuthorities(),oauth2userprincipal.getUsername(),m.getMemberId());
            resp.sendRedirect("/test/"+jwtToken.getAccesstoken()+"/");
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(jwtToken.getAccesstoken(),jwtToken.getRefreshtoken(),1000, TimeUnit.SECONDS);

        }
        else{



            Long id=memberService.membersave(new MemberDto(oauth2userprincipal.getUsername(),oauth2userprincipal.getName()));
            JwtToken jwtToken=jwtUtill.genjwt(oauth2userprincipal.getAuthorities(),oauth2userprincipal.getUsername(),id);
            resp.sendRedirect("/test/"+jwtToken.getAccesstoken()+"/");
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(jwtToken.getAccesstoken(),jwtToken.getRefreshtoken(),1000,TimeUnit.SECONDS);
        }
        */
    }


}
