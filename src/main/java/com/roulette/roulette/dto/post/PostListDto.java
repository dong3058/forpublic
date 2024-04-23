package com.roulette.roulette.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PostListDto {
    private Long memberId;
    private Long postId;
    private String title;
    private String createdTime;
    private String imgBase64;
}
