package com.roulette.roulette.auditing.dto.post;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class PreviewRequestDto {
    private Long postId;


    public PreviewRequestDto(Long postId) {
        this.postId = postId;
    }
}
