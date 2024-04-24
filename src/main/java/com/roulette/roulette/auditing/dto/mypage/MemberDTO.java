package com.roulette.roulette.auditing.dto.mypage;

import lombok.Data;

@Data
public class MemberDTO {
    private  String name;
    private  String email;

    public MemberDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public MemberDTO() {

    }
}
