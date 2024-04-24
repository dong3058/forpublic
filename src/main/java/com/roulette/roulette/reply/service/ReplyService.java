package com.roulette.roulette.reply.service;

import com.roulette.roulette.entity.Member;

public interface ReplyService {

    public void setReply(Long postId, String html, String css, String js, Member member);

    public String[] selectReplyById(Long id);

}
