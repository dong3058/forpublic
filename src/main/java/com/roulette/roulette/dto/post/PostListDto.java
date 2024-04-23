package com.roulette.roulette.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class PostListDto {
    private Long memberId;
    private String name;
    private Long postId;
    private String title;
    private LocalDateTime createTime;
    private String imgBase64;
}
