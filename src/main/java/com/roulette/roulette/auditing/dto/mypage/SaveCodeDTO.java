package com.roulette.roulette.auditing.dto.mypage;

import com.roulette.roulette.entity.Code;
import com.roulette.roulette.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SaveCodeDTO {
    private Long saveCodeId;

    private Member member;

    private Code code;

    @Builder
    public SaveCodeDTO(Long saveCodeId, Member member, Code code) {
        this.saveCodeId = saveCodeId;
        this.member = member;
        this.code = code;
    }
}
