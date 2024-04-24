package com.roulette.roulette.code.service;

import com.roulette.roulette.entity.Reply;

public interface CodeService {
    public void insertCode(String html, String css, String js, Reply reply);


}
