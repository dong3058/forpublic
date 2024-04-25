package com.roulette.roulette.reply.controller;

import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.code.request.CodeRequest;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;
    private final MemberJpaRepository memberJpaRepository;
    private final JwtUtill jwtUtill;
   /* @PostMapping
    public ResponseEntity<String> setReply(
            @RequestBody CodeRequest codeRequest,
            HttpServletRequest servletRequest
    ){

        Member member1 = memberJpaRepository.findById(1L).get();

        HttpSession session = servletRequest.getSession();

        session.setAttribute("member", member1);

        Member member = (Member) session.getAttribute("member");
        replyService.setReply(codeRequest.getPostId(), codeRequest.getHtml(),codeRequest.getCss(), codeRequest.getJs(), member);

        return ResponseEntity.ok("success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map> downloadFile(@PathVariable (value = "id") Long id) {

        String[] codeText= replyService.selectReplyById(id);

        Map<String,String> map = new HashMap<>();
        map.put("html",codeText[0]);
        map.put("css",codeText[1]);
        map.put("js",codeText[2]);

        return ResponseEntity.ok(map);

    }*/
   @Operation(summary = "답변 업로드", description = "특정 게시물에 대한 답변을 업로드합니다.")
   @ApiResponse(responseCode = "200", description = "답변이 성공적으로 업로드됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
   @PostMapping
   public ResponseEntity<String> uploadReply(
           @Parameter(description = "답변 데이터와 회원 식별자를 포함한 요청")
           @RequestBody CodeRequest codeRequest,
           HttpServletRequest servletRequest
   ){
       Member member = memberJpaRepository.findById(codeRequest.getMemberId()).get();
       replyService.setReply(codeRequest.getPostId(), codeRequest.getHtml(), codeRequest.getCss(), codeRequest.getJs(), member);

       return ResponseEntity.ok("success");
   }

    @Operation(summary = "답변 코드 다운로드", description = "답변의 HTML, CSS, JS 코드를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "코드가 성공적으로 조회됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Map> downloadReplyCode(
            @Parameter(description = "답변 ID")
            @PathVariable(value = "id") Long id
    ){
        String[] codeText = replyService.selectReplyById(id);

        Map<String, String> map = new HashMap<>();
        map.put("html", codeText[0]);
        map.put("css", codeText[1]);
        map.put("js", codeText[2]);

        return ResponseEntity.ok(map);
    }

}
