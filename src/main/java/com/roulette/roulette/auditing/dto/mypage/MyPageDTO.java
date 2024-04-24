package com.roulette.roulette.auditing.dto.mypage;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class MyPageDTO {
    private String email;
    private List<PostDTO> postList;
    private Long memberId;
    @Builder

    public MyPageDTO(String email, List<PostDTO> postList, Long memberId) {
        this.email = email;
        this.postList = postList;
        this.memberId = memberId;
    }
}
