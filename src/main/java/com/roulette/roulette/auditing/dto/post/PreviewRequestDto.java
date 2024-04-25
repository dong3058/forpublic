package com.roulette.roulette.auditing.dto.post;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class PreviewRequestDto {
    private Long replyId;


    public PreviewRequestDto(Long replyId) {
        this.replyId = replyId;
    }
}
