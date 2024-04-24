package com.roulette.roulette.myPage;


import com.roulette.roulette.auditing.dto.mypage.MemberDTO;
import com.roulette.roulette.auditing.dto.mypage.MyPageDTO;
import com.roulette.roulette.auditing.dto.mypage.SaveCodeDTO;

import java.util.List;

public interface MyPageService {
    MemberDTO getMemberDTO(Long memberId);

    MyPageDTO getMyPageData(Long memberId);

    List<SaveCodeDTO> getMyCodeData(Long memberId);

    public void insert(SaveCodeDTO saveCodeDTO);

}
