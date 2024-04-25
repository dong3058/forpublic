package com.roulette.roulette.code.service;

import com.roulette.roulette.auditing.dto.mypage.SaveCodeDTO;
import com.roulette.roulette.code.repository.CodeRepository;
import com.roulette.roulette.entity.Code;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Reply;
import com.roulette.roulette.myPage.service.MyPageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CodeServiceImpl implements CodeService {


    private final CodeRepository codeRepository;
    private final MyPageService myPageService;
    @Override
    public void insertReplyCode(String html, String css, String js, Reply reply, Member member) {


        // 경로 지정
        // 파일 이름 random 생성
        String htmlFile = UUID.randomUUID().toString();
        String htmlPath = "uploads/code/" + htmlFile;

        String cssFile = UUID.randomUUID().toString();
        String cssPath = "uploads/code/" + cssFile;

        String jsFile = UUID.randomUUID().toString();
        String jsPath = "uploads/code/" + jsFile;

        // 디렉터리 생성
        try {
            Path path = Paths.get("uploads/code");
            Files.createDirectories(path); // 필요한 모든 상위 디렉터리를 포함하여 디렉터리 생성
        } catch (IOException e) {
            log.info("Failed to create directories: " + e.getMessage());
            return; // 디렉터리 생성 실패 시 함수 종료
        }

        // text 파일 만들고 저장
        try {
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlPath));
            BufferedWriter cssWriter = new BufferedWriter(new FileWriter(cssPath));
            BufferedWriter jsWriter = new BufferedWriter(new FileWriter(jsPath));

            htmlWriter.write(html);
            cssWriter.write(css);
            jsWriter.write(js);

            htmlWriter.close();
            cssWriter.close();
            jsWriter.close();

        } catch (IOException e) {
            log.info("Error occurred while writing to file: " + e.getMessage());
            // 로깅 라이브러리로 로그 기록을 남기거나, 적절한 예외 처리를 수행할 수 있습니다.
        }

        // Code 객체로 만든후 repository에 저장
        Code code = Code.builder()
                .htmlCodeUrl(htmlFile)
                .cssCodeUrl(cssFile)
                .jsCodeUrl(jsFile)
                .reply(reply)
                .build();

        reply.add(code);

        log.info(reply.getCode().getHtmlCodeUrl());
        log.info(reply.getCode().getCssCodeUrl());
        log.info(reply.getCode().getJsCodeUrl());

        SaveCodeDTO saveCodeDTO = SaveCodeDTO.builder()
                .code(code)
                .member(member)
                .build();


        myPageService.insert(saveCodeDTO);
        codeRepository.save(code);


    }

    @Override
    public void insertCode(String html, String css, String js, Member member) {
        // 경로 지정
        // 파일 이름 random 생성
        String htmlFile = UUID.randomUUID().toString();
        String htmlPath = "uploads/code/" + htmlFile;

        String cssFile = UUID.randomUUID().toString();
        String cssPath = "uploads/code/" + cssFile;

        String jsFile = UUID.randomUUID().toString();
        String jsPath = "uploads/code/" + jsFile;

        // 디렉터리 생성
        try {
            Path path = Paths.get("uploads/code");
            Files.createDirectories(path); // 필요한 모든 상위 디렉터리를 포함하여 디렉터리 생성
        } catch (IOException e) {
            log.info("Failed to create directories: " + e.getMessage());
            return; // 디렉터리 생성 실패 시 함수 종료
        }

        // text 파일 만들고 저장
        try {
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlPath));
            BufferedWriter cssWriter = new BufferedWriter(new FileWriter(cssPath));
            BufferedWriter jsWriter = new BufferedWriter(new FileWriter(jsPath));

            htmlWriter.write(html);
            cssWriter.write(css);
            jsWriter.write(js);

            htmlWriter.close();
            cssWriter.close();
            jsWriter.close();

        } catch (IOException e) {
            log.info("Error occurred while writing to file: " + e.getMessage());
            // 로깅 라이브러리로 로그 기록을 남기거나, 적절한 예외 처리를 수행할 수 있습니다.
        }

        // Code 객체로 만든후 repository에 저장
        Code code = Code.builder()
                .htmlCodeUrl(htmlFile)
                .cssCodeUrl(cssFile)
                .jsCodeUrl(jsFile)
                .build();

        log.info(code.getHtmlCodeUrl());
        log.info(code.getCssCodeUrl());
        log.info(code.getJsCodeUrl());


        SaveCodeDTO saveCodeDTO = SaveCodeDTO.builder()
                .code(code)
                .member(member)
                .build();


        myPageService.insert(saveCodeDTO);
        codeRepository.save(code);

    }

    @Override
    public String[] selectCodeById(Long id) {
        Code code = codeRepository.findById(id).get();
        String filePath = "uploads/code";

        String[] str = {"","",""};

        File htmlFile = new File(filePath,code.getHtmlCodeUrl());
        File cssFile = new File(filePath,code.getCssCodeUrl());
        File jsFile = new File(filePath,code.getJsCodeUrl());

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
    /*@Override
    public void insertCode(String html, String css, String js, Reply reply) {

        // 경로 지정
        // 파일 이름 random 생성
        String htmlFile = UUID.randomUUID().toString();
        String htmlPath = "uploads/code/" + htmlFile;

        String cssFile = UUID.randomUUID().toString();
        String cssPath = "uploads/code/" + cssFile;

        String jsFile = UUID.randomUUID().toString();
        String jsPath = "uploads/code/" + jsFile;

        // text 파일 만들고 저장
        try {
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlPath));
            BufferedWriter cssWriter = new BufferedWriter(new FileWriter(cssPath));
            BufferedWriter jsWriter = new BufferedWriter(new FileWriter(jsPath));

            htmlWriter.write(html);
            cssWriter.write(css);
            jsWriter.write(js);

            htmlWriter.close();
            cssWriter.close();
            jsWriter.close();

        } catch (IOException e) {
            log.info("Error occurred while writing to file: " + e.getMessage());
            // 로깅 라이브러리로 로그 기록을 남기거나, 적절한 예외 처리를 수행할 수 있습니다.
        }

        // Code 객체로 만든후 repository에 저장
        Code code = Code.builder()
                .htmlCodeUrl(htmlFile)
                .cssCodeUrl(cssFile)
                .jsCodeUrl(jsFile)
                .reply(reply)
                .build();

        reply.add(code);

        log.info(reply.getCode().getHtmlCodeUrl());
        log.info(reply.getCode().getCssCodeUrl());
        log.info(reply.getCode().getJsCodeUrl());

        codeRepository.save(code);


    }*/


}
