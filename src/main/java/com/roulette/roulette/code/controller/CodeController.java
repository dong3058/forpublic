package com.roulette.roulette.code.controller;

import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.code.request.SaveCodeRequest;
import com.roulette.roulette.code.service.CodeService;
import com.roulette.roulette.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/code")
public class CodeController {
    private final CodeService codeService;
    private final MemberJpaRepository memberJpaRepository;
    private final JwtUtill jwtUtill;

    @PostMapping
    public ResponseEntity<String> uploadCode(SaveCodeRequest saveCodeRequest, HttpServletRequest servletRequest){

       String token = servletRequest.getHeader("Authorization").substring(7);
       Member member = memberJpaRepository.findById(jwtUtill.getidfromtoken(token)).get();

        codeService.insertCode(saveCodeRequest.getHtml(),saveCodeRequest.getCss(),saveCodeRequest.getJs(),member);

        return ResponseEntity.ok("success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map> downloadCode(@PathVariable(value = "id") Long id) {

        String[] codeText= codeService.selectCodeById(id);

        Map<String,String> map = new HashMap<>();
        map.put("html",codeText[0]);
        map.put("css",codeText[1]);
        map.put("js",codeText[2]);

        return ResponseEntity.ok(map);

    }
}
