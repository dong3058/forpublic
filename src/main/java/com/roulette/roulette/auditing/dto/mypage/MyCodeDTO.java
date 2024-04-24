package com.roulette.roulette.auditing.dto.mypage;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class MyCodeDTO {
    private List<CodeDTO> codeList;

    @Builder
    public MyCodeDTO(List<CodeDTO> codeList) {
        this.codeList = codeList;
    }
}
