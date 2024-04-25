package com.roulette.roulette.myPage.controller;

import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.auditing.dto.mypage.MemberDTO;
import com.roulette.roulette.auditing.dto.mypage.MyPageDTO;
import com.roulette.roulette.auditing.dto.mypage.SaveCodeDTO;
import com.roulette.roulette.myPage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    private final JwtUtill jwtUtill;


    @Operation(summary = "myPage로 가기", description = "마이페이지로 가면 code : 200 보내준다")
    @ApiResponse(responseCode = "200", description = "", content = @Content(mediaType = "application/json"))
    @GetMapping()
    @ResponseBody
    public Map<String, Integer> goMyPage(){
        Map<String, Integer> response = new HashMap<>();
        response.put("code", 200);
        return response;
    }

    @Operation(summary = "내 정보 조회", description = "내 정보를 member_id로 찾아와서 DTO로준다")
    @ApiResponse(responseCode = "200", description = "", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDTO .class)))
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
    @Operation(summary = "내 질문 목록 리스트 가지고 오기", description = "내질문 목록들을 Post에서 가져와서 DTO로 던진다")
    @ApiResponse(responseCode = "200", description = "", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyPageDTO .class)))
    @GetMapping("/list")
    public ResponseEntity<MyPageDTO> getMyPost(HttpServletRequest req){
        String access_token=req.getHeader("Authorization").substring(7);
        Long member_id = jwtUtill.getidfromtoken(access_token);
        MyPageDTO myPageDTO = myPageService.getMyPageData(member_id);
        if (myPageDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(myPageDTO, HttpStatus.OK);
    }

    // 내가 저장한 코드 목록보기
    @Operation(summary = "내가 저장한 코드 목록 보기", description = "내가 저장한 코드 목록을 saveCode에서 가져와 던진다")
    @ApiResponse(responseCode = "200", description = "", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaveCodeDTO.class)))
    @GetMapping("/code")
    public ResponseEntity<List<SaveCodeDTO>> getMyCode(HttpServletRequest req){
        String access_token=req.getHeader("Authorization").substring(7);
        Long member_id = jwtUtill.getidfromtoken(access_token);
        List<SaveCodeDTO> saveCodeDTOS = myPageService.getMyCodeData(member_id);
        if (saveCodeDTOS == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(saveCodeDTOS, HttpStatus.OK);
    }
}
