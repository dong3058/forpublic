package com.roulette.roulette.code.response;

import com.roulette.roulette.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
public class CodeResponse {
    private File code;
    private Member member;


    @Builder
    public CodeResponse(File code, Member member) {
        this.code = code;
        this.member = member;
    }
}
