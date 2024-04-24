package com.roulette.roulette.auditing.dto.mypage;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CodeDTO {
    private Long codeId;

    private String htmlCodeUrl;

    private String cssCodeUrl;

    private String jsCodeUrl;

    @Builder
    public CodeDTO(Long codeId, String htmlCodeUrl, String cssCodeUrl, String jsCodeUrl) {
        this.codeId = codeId;
        this.htmlCodeUrl = htmlCodeUrl;
        this.cssCodeUrl = cssCodeUrl;
        this.jsCodeUrl = jsCodeUrl;
    }
}
