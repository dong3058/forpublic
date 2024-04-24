package com.roulette.roulette.code.request;

import lombok.Data;

@Data
public class SaveCodeRequest {
    private Long postId;
    private String html;
    private String css;
    private String js;
}
