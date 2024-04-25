package com.roulette.roulette.post.controller;

import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.aboutlogin.repository.MemberRepository;
import com.roulette.roulette.auditing.dto.post.*;
import com.roulette.roulette.code.request.CodeRequest;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.post.service.PostService;
import com.roulette.roulette.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Operation(summary = "최신 게시글 목록 조회", description = "페이지 단위로 최신 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 조회됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostListDto.class)))
    @GetMapping("/list/{page}")
    public Page<PostListDto> getPostList(@Parameter(description = "페이지 번호") @PathVariable int page) {
        return postService.getRecentPosts(page, 10);
    }

    @Operation(summary = "특정 게시글 조회", description = "ID를 기반으로 특정 게시글과 해당 답변들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostAndReplyListDto.class)))
    @GetMapping("/{post_id}")
    public PostAndReplyListDto getPost(@Parameter(description = "게시글 ID") @PathVariable Long post_id) {
        return postService.getPostById(post_id).get();
    }

    @Operation(summary = "답변 선택", description = "게시글에 대한 답변을 최종적으로 선택합니다.")
    @ApiResponse(responseCode = "200", description = "답변이 성공적으로 선택됨")
    @PostMapping("/choice")
    public ResponseEntity<String> chooseReply(@RequestBody(description = "답변 선택에 필요한 요청 데이터", required = true, content = @Content(schema = @Schema(implementation = CodeRequest.class))) CodeRequest codeRequest) {
        postService.setPostChoiceComplete(codeRequest.getPostId());
        Member member = memberRepository.findById(codeRequest.getMemberId()).get();
        replyService.setReply(codeRequest.getPostId(), codeRequest.getHtml(), codeRequest.getCss(), codeRequest.getJs(), member);

        return ResponseEntity.ok("success");
    }

    @Operation(summary = "새 게시글 작성", description = "이미지를 포함한 새 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글이 성공적으로 생성됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AskPostResponseDto.class)))
    @PostMapping(value = "/ask", consumes = "multipart/form-data")
    public AskPostResponseDto askPost(
            @Parameter(description = "게시글 작성에 필요한 데이터: 제목, 내용, 회원 ID, 이미지 파일")
            @ModelAttribute AskPostRequestDto request) throws IOException {

        Long postId = postService.createPost(request);
        return new AskPostResponseDto(postId);
    }

    @Operation(summary = "게시글 미리보기", description = "최종 제출 전 게시글의 미리보기를 제공합니다.")
    @ApiResponse(responseCode = "200", description = "미리보기 생성됨")
    @PostMapping(value = "/preview")
    public ResponseEntity<Map<String, String>> previewPost(
            @Parameter(description = "답변 ID; 해당 답변의 HTML, CSS, JS 코드를 미리 볼 수 있습니다.")
            @RequestBody PreviewRequestDto request) {
        log.info("-------id check:{}",request.getReplyId());
        String[] codeText = replyService.selectReplyById(request.getReplyId());

        Map<String, String> map = new HashMap<>();
        map.put("html", codeText[0]);
        map.put("css", codeText[1]);
        map.put("js", codeText[2]);

        return ResponseEntity.ok(map);
    }
}
