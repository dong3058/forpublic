package com.roulette.roulette.aboutlogin;

import com.roulette.roulette.aboutlogin.domaindto.MemberDto;
import com.roulette.roulette.aboutlogin.jwt.JwtToken;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.aboutlogin.repository.MemberRepository;
import com.roulette.roulette.aboutlogin.userinfo.CustomUserDetail;
import com.roulette.roulette.aboutlogin.userinfo.Kakaouserdata;
import com.roulette.roulette.aboutlogin.userinfo.Oauth2userprincipal;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.aboutlogin.exceptions.AccessTokenRefresh;
import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class Oauth2Controller {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoclientid;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakakoredirecturi;


    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenuri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userinfouri;
    private RedisTemplate<String,String> redisTemplate;

    private JwtUtill jwtUtill;

    private MemberService memberService;


    private MemberRepository memberRepository;

    private MemberJpaRepository memberJpaRepository;


    @Autowired
    public Oauth2Controller(@Qualifier("redisTemplate") RedisTemplate<String,String> redisTemplate, JwtUtill jwtUtill, MemberService memberService,MemberRepository memberRepository
    ,MemberJpaRepository memberJpaRepository){
        this.redisTemplate=redisTemplate;

        this.jwtUtill=jwtUtill;
        this.memberService=memberService;
        this.memberRepository=memberRepository;
        this.memberJpaRepository=memberJpaRepository;
    }


    @PostMapping("/reqlogin")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> loginreal(@RequestBody Access_Code accessCode,HttpServletRequest req, HttpServletResponse resp){
        log.info("-------------access_code---------:{}",accessCode.getAccess_code());
        log.info("kakakoredirdct:{}",kakakoredirecturi);
        log.info("kakaoid:{}",kakaoclientid);
        RestTemplate rt = new RestTemplate();


        String proxyHost = "krmp-proxy.9rum.cc";
        int proxyPort = 3128;


        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();


        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        requestFactory.setProxy(proxy);


        rt.setRequestFactory(requestFactory);
        String code=accessCode.getAccess_code();
        log.info("-----------check1-------------");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> accessTokenParams = accessTokenParams("authorization_code",kakaoclientid,code,kakakoredirecturi);
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, headers);
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                tokenuri,
                HttpMethod.POST,
                accessTokenRequest,
                String.class);

        log.info("-----------check2-------------");




        try {

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(accessTokenResponse.getBody());

            String header = "Bearer " + jsonObject.get("access_token");
            System.out.println("header = " + header);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", header);


            String responseBody=getproxy2(userinfouri,requestHeaders,header);

            JSONObject profile = (JSONObject) jsonParser.parse(responseBody);
            JSONObject properties = (JSONObject) profile.get("properties");
            JSONObject kakao_account = (JSONObject) profile.get("kakao_account");


            Long loginId = (Long) profile.get("id");
            String email = (String) kakao_account.get("email");
            String userName = (String) properties.get("nickname");

            //Optional<Member> member=memberService.findmemberbyemail(email);

            Optional<Member> member=memberJpaRepository.findById(loginId);


            List<Object> token_id_list = gettokenandresponse(email, userName, member, resp,req);






            log.info("다시만든 제발 되라 으어ㅜ퍼ㅜtoken:{}",token_id_list.get(0));

            return new ResponseEntity<>(new AccessTokenRefresh((String) token_id_list.get(0),"200","/",(Long) token_id_list.get(1)),HttpStatus.OK);


        } catch (Exception e) {
            log.info("에러가터지면안된다 오지말아다오.");
            e.printStackTrace();
        }


        log.info("여기는 오면안되낟 그러지말아다오");
        return null;

    }



    public List<Object> gettokenandresponse(String email, String username, Optional<Member> member, HttpServletResponse resp, HttpServletRequest req) throws IOException{
        if(member.isPresent()){
            Member m=member.get();

            JwtToken jwtToken=jwtUtill.genjwt(username,m.getMemberId());

            HttpSession session=req.getSession();
            log.info("session exist:{}",session);
            session.setAttribute("member",m);
            List<Object> obj=new ArrayList<>();
            obj.add(jwtToken.getAccesstoken());
            obj.add(m.getMemberId());
            return obj;
        }
        else{

            Long id=memberService.membersave(new MemberDto(email,username));

            HttpSession session=req.getSession();
            Member m=memberJpaRepository.findById(id).get();
            session.setAttribute("member",m);



            JwtToken jwtToken=jwtUtill.genjwt(username,id);

            List<Object> obj=new ArrayList<>();
            obj.add(jwtToken.getAccesstoken());
            obj.add(m.getMemberId());
            return obj;
        }

    }



    public MultiValueMap<String, String> accessTokenParams(String grantType, String clientId,String code,String redirect_uri) {
        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", grantType);
        accessTokenParams.add("client_id", clientId);
        accessTokenParams.add("code", code);
        accessTokenParams.add("redirect_uri", redirect_uri);
        return accessTokenParams;
    }


    /*@GetMapping("/kakaologin")
    public void kakaologin(HttpServletResponse resp)throws IOException {
        log.info("kakakologin check");
        resp.sendRedirect("/oauth2/authorization/kakao");
    }




    @GetMapping("/googlelogin")
    public void googlelogin(HttpServletResponse resp)throws IOException {
        log.info("google login check");
        resp.sendRedirect("/oauth2/authorization/google");
    }
*/
    @GetMapping("/api1")
    @ResponseBody
    public String api1(){
        log.info("kakaoredirecturl:{}",kakakoredirecturi);
        log.info("kakaoid:{}",kakaoclientid);
        return "api1";
    }

    @GetMapping("/logouts")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> logout(HttpServletRequest req){
        String access_token=req.getHeader("Authorization").substring(7);
        HttpSession session=req.getSession(false);

        log.info("session check in logouts:{}",session);
        session.invalidate();


        log.info("로그아웃용 accesstoken:{}",access_token);
        redisTemplate.delete(access_token);
        log.info("--------로그아웃 성공--------------");
        return new ResponseEntity<>(new AccessTokenRefresh(null,"200","/",null), HttpStatus.OK);

    }
    @GetMapping("/test/{accesstoken}/{redirecturl}")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> test(@PathVariable(name="accesstoken") String token, @PathVariable("redirecturl") String url){
        log.info("testurl로 성공적인 데이터 전송 성공");

        if(token.equals("no")){
            return new ResponseEntity<>(new AccessTokenRefresh(null,"400",url,null),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new AccessTokenRefresh(token,"200",url,null),HttpStatus.OK);
    }






    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {

            log.info("read try");
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            log.info("read responsebody:{}",responseBody);
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }


    private static String getproxy2(String apiUrl, Map<String, String> requestHeaders,String header) {
        HttpURLConnection con = connect2(apiUrl);
        try {
            log.info("try in get");
            con.setRequestMethod("GET");
            log.info("con.setRequestMethod");

            /*for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                log.info("get메서드 중간의 for문");
                log.info("값체크con:{}", con);
                con.setRequestProperty(header.getKey(), header.getValue());
                log.info("값체크:{}", con.getRequestProperties());
            }*/


            con.setRequestProperty("Authorization", header);



            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출

                return readBody(con.getInputStream());
            } else { // 에러 발생

                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
    private static HttpURLConnection connect2(String apiUrl) {
         String PROXY_HOST = "krmp-proxy.9rum.cc";
         int PROXY_PORT = 3128;
        try {

            URL url = new URL(apiUrl);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            log.info("connect check-------------------");
            return (HttpURLConnection) url.openConnection(proxy);
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }






}
