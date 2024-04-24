package com.roulette.roulette.myPage;

import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.dto.mypage.MemberDTO;
import com.roulette.roulette.dto.mypage.MyCodeDTO;
import com.roulette.roulette.dto.mypage.MyPageDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    private final JwtUtill jwtUtill;


    //마이페이지 이동

    @GetMapping()
    @ResponseBody
    public Map<String, Integer> goMyPage(){
        Map<String, Integer> response = new HashMap<>();
        response.put("code", 200);
        return response;
    }

    // 내정보 조회하기
    @GetMapping("/member")
    public ResponseEntity<MemberDTO> getAllMembers(HttpServletRequest req) {
        String access_token=req.getHeader("Authorization").substring(7);
        Long member_id = jwtUtill.getidfromtoken(access_token);
        MemberDTO memberDTO = myPageService.getMemberDTO(member_id);
        if (memberDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }

    // 내 질문 목록 리스트에서 email포함 해서 DTO만들기
    @GetMapping("/list")
    public ResponseEntity<MyPageDTO> getMyPost(HttpServletRequest req){
        String access_token=req.getHeader("Authorization");
        Long member_id = jwtUtill.getidfromtoken(access_token);
        MyPageDTO myPageDTO = myPageService.getMyPageData(member_id);
        if (myPageDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(myPageDTO, HttpStatus.OK);
    }

    // 내가 저장한 코드 목록보기
    @GetMapping("/code")
    public ResponseEntity<MyCodeDTO> getMyCode(HttpServletRequest req){
        String access_token=req.getHeader("Authorization");
        Long member_id = jwtUtill.getidfromtoken(access_token);
        MyCodeDTO myCodeDTO = myPageService.getMyCodeData(member_id);
        if (myCodeDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(myCodeDTO, HttpStatus.OK);
    }
}
