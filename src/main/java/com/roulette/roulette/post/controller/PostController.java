package com.roulette.roulette.post.controller;

import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.aboutlogin.repository.MemberRepository;
import com.roulette.roulette.auditing.dto.post.*;
import com.roulette.roulette.code.request.CodeRequest;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.post.service.PostService;
import com.roulette.roulette.reply.service.ReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list/{page}")
    public Page<PostListDto> getPostList(@PathVariable int page) {
        return postService.getRecentPosts(page, 10);
    }

    @GetMapping("/{post_id}")
    public PostAndReplyListDto getPost(@PathVariable Long post_id) {
        return postService.getPostById(post_id).get(); //Optional로 주기 때문에

    }

    @PostMapping("/choice")
    public ResponseEntity<String> chooseReply(@RequestBody CodeRequest codeRequest) {
        postService.setPostChoiceComplete(codeRequest.getPostId());
        Member member = memberRepository.findById(codeRequest.getMemberId()).get();
        replyService.setReply(codeRequest.getPostId(), codeRequest.getHtml(),codeRequest.getCss(), codeRequest.getJs(), member);

        return ResponseEntity.ok("success");
    }

    @PostMapping(value = "/ask", consumes = "multipart/form-data")
    public AskPostResponseDto askPost(@ModelAttribute AskPostRequestDto request) throws IOException {
        Long postId = postService.createPost(request);
        return new AskPostResponseDto(postId);
    }

    @PostMapping(value = "/preview")
    public ResponseEntity<Map<String, String>> previewPost(@RequestBody PreviewRequestDto request){


        log.info("----postpreview id check:{}",request.getReplyId());
        String[] codeText= replyService.selectReplyById(request.getReplyId());

        Map<String,String> map = new HashMap<>();
        map.put("html",codeText[0]);
        map.put("css",codeText[1]);
        map.put("js",codeText[2]);

        return ResponseEntity.ok(map);
    }
}
