package com.roulette.roulette.dto.post;

import com.roulette.roulette.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostAndReplyListDto {
    private Long postId;
    private String name;
    private Long memberId;
    private String title;
    private String content;
    private String imgBase64;
    private LocalDateTime createTime;
    private List<Reply> replyList;
}
