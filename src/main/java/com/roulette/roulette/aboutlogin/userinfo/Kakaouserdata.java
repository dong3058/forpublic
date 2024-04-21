package com.roulette.roulette.aboutlogin.userinfo;

import lombok.Data;

@Data
public class Kakaouserdata {

    private String username;
    private String email;


    public Kakaouserdata(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
