package com.roulette.roulette.reply.service;

import com.roulette.roulette.code.service.CodeService;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.entity.Reply;
import com.roulette.roulette.post.repository.PostRepository;
import com.roulette.roulette.reply.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final CodeService codeService;
    @Override
    public void setReply(Long postId, String html, String css, String js, Member member) {
        Post post = postRepository.findById(postId).get();

        Reply reply =  Reply.builder()
                .post(post)
                .member(member)
                .build();

        codeService.insertCode(html, css, js, reply);


        replyRepository.save(reply);

    }

    @Override
    public String[] selectReplyById(Long id) {
            Reply reply = replyRepository.findById(id).get();
            String filePath = "uploads/code";

            String[] str = {"","",""};

            File htmlFile = new File(filePath,reply.getCode().getHtmlCodeUrl());
            File cssFile = new File(filePath,reply.getCode().getCssCodeUrl());
            File jsFile = new File(filePath,reply.getCode().getJsCodeUrl());

            BufferedReader html;
            BufferedReader css;
            BufferedReader js;

            try{
                FileReader htmlReader = new FileReader(htmlFile);
                FileReader cssReader = new FileReader(cssFile);
                FileReader jsReader = new FileReader(jsFile);

                html = new BufferedReader(htmlReader);
                css = new BufferedReader(cssReader);
                js = new BufferedReader(jsReader);

                String line = "";
                while ((line = html.readLine()) != null){
                    str[0] += line+"\n";
                }
                while ((line = css.readLine()) != null){
                    str[1] += line+"\n";
                }
                while ((line = js.readLine()) != null){
                    str[2] += line+"\n";
                }
            } catch (IOException e) {
                System.err.println("Unable to read the file: " + e.getMessage());
                return null;  // 또는 적절한 예외 처리
            }
            log.info("실행");



        return str;
    }
}
