package com.roulette.roulette.code.service;

import com.roulette.roulette.code.repository.CodeRepository;
import com.roulette.roulette.entity.Code;
import com.roulette.roulette.entity.Reply;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CodeServiceImpl implements CodeService {


    private final CodeRepository codeRepository;
    @Override
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


    }


}
