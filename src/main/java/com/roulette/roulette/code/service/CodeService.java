package com.roulette.roulette.code.service;

import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Reply;

public interface CodeService {
    /*public void insertCode(String html, String css, String js, Reply reply);

    public void insertReplyCode(String html, String css, String js, Reply reply, Member member);

    public String[] selectCodeById(Long id);*/

    public void insertReplyCode(String html, String css, String js, Reply reply,Member member);

    public void insertCode(String html, String css, String js, Member member);

    public String[] selectCodeById(Long id);



}
