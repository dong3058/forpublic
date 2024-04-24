package com.roulette.roulette.aboutlogin.exceptions;

import lombok.Data;

@Data
public class AccessTokenRefresh {
    private String code;
    private String access_token;
    private String redirect_url;

    private Long member_id;


    private String user_image_url;

    public AccessTokenRefresh(String access_token, String code, String redirect_url,Long id,String url) {
        this.access_token = access_token;
        this.code = code;
        this.redirect_url = redirect_url;
        this.member_id=id;
        this.user_image_url=url;
    }
}