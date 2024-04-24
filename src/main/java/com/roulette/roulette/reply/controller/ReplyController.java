package com.roulette.roulette.reply.controller;

import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.code.request.CodeRequest;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.reply.service.ReplyService;
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
    @PostMapping
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

    }

}
