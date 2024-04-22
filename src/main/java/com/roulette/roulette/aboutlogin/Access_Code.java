package com.roulette.roulette.aboutlogin;


import lombok.Data;

@Data
public class Access_Code {

    private String access_code;

    public Access_Code(String access_code) {
        this.access_code = access_code;
    }

    public Access_Code() {
    }
}
