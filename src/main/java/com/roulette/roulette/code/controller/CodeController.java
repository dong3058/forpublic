package com.roulette.roulette.code.controller;

import com.roulette.roulette.aboutlogin.jwt.JwtUtill;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.code.request.SaveCodeRequest;
import com.roulette.roulette.code.service.CodeService;
import com.roulette.roulette.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

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

    @Operation(summary = "코드 업로드", description = "사용자 인증 후 HTML, CSS, JS 코드를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "코드 저장 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping
    public ResponseEntity<String> uploadCode(
            @RequestBody(description = "저장할 코드 정보와 게시물 ID를 포함한 요청") SaveCodeRequest saveCodeRequest,
            HttpServletRequest servletRequest) {

        String token = servletRequest.getHeader("Authorization").substring(7);
        Member member = memberJpaRepository.findById(jwtUtill.getidfromtoken(token)).get();

        codeService.insertCode(saveCodeRequest.getHtml(), saveCodeRequest.getCss(), saveCodeRequest.getJs(), member);

        return ResponseEntity.ok("success");
    }

    @Operation(summary = "코드 다운로드", description = "주어진 ID에 해당하는 HTML, CSS, JS 코드를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "코드 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Map> downloadCode(
            @Parameter(description = "코드 ID") @PathVariable(value = "id") Long id) {

        String[] codeText = codeService.selectCodeById(id);

        Map<String, String> map = new HashMap<>();
        map.put("html", codeText[0]);
        map.put("css", codeText[1]);
        map.put("js", codeText[2]);

        return ResponseEntity.ok(map);
    }
}
