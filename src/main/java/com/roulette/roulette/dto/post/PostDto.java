package com.roulette.roulette.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private Long memberId;
    private String content;
    private String imgBase64;
}
