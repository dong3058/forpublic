package com.roulette.roulette.aboutlogin;

import com.roulette.roulette.aboutlogin.domaindto.MemberDto;
import com.roulette.roulette.aboutlogin.jwt.JwtToken;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class Oauth2Controller {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoclientid;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakakoredirecturi;
   // private String kakakoredirecturi="https://k9bceeba41403a.user-app.krampoline.com/login/oauth2/code/kakao";


    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenuri;


    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userinfouri;
    private RedisTemplate<String,String> redisTemplate;

    private JwtUtill jwtUtill;

    private MemberService memberService;


    private MemberRepository memberRepository;

    @Autowired
    public Oauth2Controller(@Qualifier("redisTemplate") RedisTemplate<String,String> redisTemplate, JwtUtill jwtUtill, MemberService memberService,MemberRepository memberRepository){
        this.redisTemplate=redisTemplate;

        this.jwtUtill=jwtUtill;
        this.memberService=memberService;
        this.memberRepository=memberRepository;
    }


    @GetMapping("/reqlogin/{code}")
    @ResponseBody
    public ResponseEntity<AccessTokenRefresh> loginreal(@PathVariable(name="code")String code,HttpServletResponse resp){
        log.info("-------------cocde----------:{}",code);
        log.info("kakakoredirdct:{}",kakakoredirecturi);
        log.info("kakaoid:{}",kakaoclientid);
        RestTemplate rt = new RestTemplate();
        //HttpHeaders headers = new HttpHeaders();





        String proxyHost = "krmp-proxy.9rum.cc";
        int proxyPort = 3128;

        // RestTemplate을 생성합니다.
        //RestTemplate restTemplate = new RestTemplate();

        // SimpleClientHttpRequestFactory를 생성합니다.
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // 프록시 설정을 합니다.
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        requestFactory.setProxy(proxy);

        // RestTemplate에 설정합니다.
        rt.setRequestFactory(requestFactory);

        // HTTP 요청을 보냅니다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> accessTokenParams = accessTokenParams("authorization_code",kakaoclientid,code,kakakoredirecturi);
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, headers);
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                tokenuri,
                HttpMethod.POST,
                accessTokenRequest,
                String.class);











        /*headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> accessTokenParams = accessTokenParams("authorization_code",kakaoclientid,code,kakakoredirecturi);
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, headers);
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                tokenuri,
                HttpMethod.POST,
                accessTokenRequest,
                String.class);*/

        try {


            log.info("try");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(accessTokenResponse.getBody());
            //session.setAttribute("Authorization", jsonObject.get("access_token"));
            String header = "Bearer " + jsonObject.get("access_token");
            System.out.println("header = " + header);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", header);
            //String responseBody = get(userinfouri, requestHeaders);

            String responseBody=getproxy2(userinfouri,requestHeaders,header);

            JSONObject profile = (JSONObject) jsonParser.parse(responseBody);
            JSONObject properties = (JSONObject) profile.get("properties");
            JSONObject kakao_account = (JSONObject) profile.get("kakao_account");


            Long loginId = (Long) profile.get("id");
            String email = (String) kakao_account.get("email");
            String userName = (String) properties.get("nickname");

            Optional<Member> member=memberService.findmemberbyemail(email);


            log.info("hello world");

            String token = gettokenandresponse(email, userName, member, resp);


            log.info("다시만든 제발 되라 으어ㅜ퍼ㅜtoken:{}",token);

            return new ResponseEntity<>(new AccessTokenRefresh(token,"400","/"),HttpStatus.BAD_REQUEST);



            /*(CustomUserDetail customUserDetail=new CustomUserDetail(new Kakaouserdata(userName,email));


            User kakaoUser = new UserRequest("social_" + loginId, encode.encode("카카오"), userName, email).kakaoOAuthToEntity();
            if (userRepository.existsByLoginId(kakaoUser.getLoginId()) == false) {
                userRepository.save(kakaoUser);
            }
            String access_token = tokenProvider.create(new PrincipalDetails(kakaoUser));
            res.setHeader("Authorization", "Bearer "+access_token);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }



    public String gettokenandresponse(String email,String username,Optional<Member> member, HttpServletResponse resp) throws IOException{
        if(member.isPresent()){
            Member m=member.get();
            JwtToken jwtToken=jwtUtill.genjwt(username,m.getMemberId());
            //resp.sendRedirect("/test/"+jwtToken.getAccesstoken()+"/");
            log.info("---------------start------------");
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            log.info("aboutoperations:{}",operations);
            operations.set(jwtToken.getAccesstoken(),jwtToken.getRefreshtoken(),1000, TimeUnit.SECONDS);
            return jwtToken.getAccesstoken();
        }
        else{

            Long id=memberService.membersave(new MemberDto(email,username));
            JwtToken jwtToken=jwtUtill.genjwt(username,id);
            //resp.sendRedirect("/test/"+jwtToken.getAccesstoken()+"/");
            log.info("---------------start------------");
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            log.info("aboutoperations:{}",operations);
            operations.set(jwtToken.getAccesstoken(),jwtToken.getRefreshtoken(),1000,TimeUnit.SECONDS);
            return jwtToken.getAccesstoken();
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




    @GetMapping("/kakaologin")
    public void kakaologin(HttpServletResponse resp)throws IOException {
        log.info("kakakologin check");
        resp.sendRedirect("/oauth2/authorization/kakao");
    }




    @GetMapping("/googlelogin")
    public void googlelogin(HttpServletResponse resp)throws IOException {
        log.info("google login check");
        resp.sendRedirect("/oauth2/authorization/google");
    }

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

            log.info("값체크 con:{}",con.getHeaderField("Authorization"));

            int responseCode = con.getResponseCode();
            log.info("responsecode:{}", responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                log.info("정상 호출 in get 메서드");
                return readBody(con.getInputStream());
            } else { // 에러 발생
                log.info("에러발생 in get 메서드");
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
            log.info("url try");
            URL url = new URL(apiUrl);
            log.info("url:{}", url.openConnection(Proxy.NO_PROXY));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            return (HttpURLConnection) url.openConnection(proxy);
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }



    /*private static String getproxy(String apiUrl, Map<String, String> requestHeaders,String header)throws IOException {
        // 프록시 설정
        System.setProperty("http.proxyHost", "krmp-proxy.9rum.cc");
        System.setProperty("http.proxyPort", "3128");

        HttpURLConnection con = connect(apiUrl);
        log.info("con객체 데이터:{}",con.getResponseCode());
        try {
            log.info("try in get");
            con.setRequestMethod("GET");
            log.info("con.setrequestmethod");
            for (Map.Entry<String, String> headers : requestHeaders.entrySet()) {
                log.info("get메서드 중간의 for문");
                log.info("값체크con:{}", con);
                log.info("헤더값 체크 :{}  value:{}",headers.getKey(),headers.getValue());
                con.setRequestProperty(headers.getKey(), headers.getValue());

                log.info("값체크:{}", con.getRequestProperties());
                log.info("값체크 22:{}",con.getRequestProperty("Authorization"));
            }

            con.setRequestProperty("Authorization",header);

            log.info("토큰값:{}",header);
            log.info("갑체크333:{}",con.getHeaderField("Authorization"));
            log.info("conreqsponsecode:{}",con.getResponseCode());
            int responseCode = con.getResponseCode();
            log.info("responsecode:{}", responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출

                log.info("정상 호출 in get 메서드");
                return readBody(con.getInputStream());
            } else { // 에러 발생
                log.info("에러발생 in get 메서드");
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }*/







    /*private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {


            log.info("try in get");
            con.setRequestMethod("GET");
            log.info("con.setrequestmethod");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                log.info("get메서드 중간의 for문");
                log.info("값체크con:{}",con);
                con.setRequestProperty(header.getKey(), header.getValue());

                log.info("값체크:{}",con.getRequestProperties());
            }


            int responseCode = con.getResponseCode();
            log.info("responsecode:{}",responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출

                log.info("정상 호출 in get 메서드");
                return readBody(con.getInputStream());
            } else { // 에러 발생
                log.info("에러발생 in get 메서드");
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {


            log.info("url try");
            URL url = new URL(apiUrl);
            log.info("url:{}",url.openConnection());
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }*/




}
