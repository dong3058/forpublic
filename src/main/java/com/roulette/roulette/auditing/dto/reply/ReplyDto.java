package com.roulette.roulette.auditing.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {
    private Long replyId;
    private String memberName;
    private Long memberId;
    private LocalDateTime createTime;
}
