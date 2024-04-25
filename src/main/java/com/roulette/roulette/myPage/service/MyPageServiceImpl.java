package com.roulette.roulette.myPage.service;

import com.roulette.roulette.auditing.dto.mypage.MemberDTO;
import com.roulette.roulette.auditing.dto.mypage.MyPageDTO;
import com.roulette.roulette.auditing.dto.mypage.PostDTO;
import com.roulette.roulette.auditing.dto.mypage.SaveCodeDTO;
import com.roulette.roulette.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Transactional
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
    private final ServiceInjection serviceInjection;
    private final Assistant assistant;

    //내정보 조회하기
    @Override
    public MemberDTO getMemberDTO(Long member_id){
        Member member = serviceInjection.findById(member_id);
        return member != null ? new MemberDTO(member.getName(), member.getEmail()) : null;
    }

    //내가한 질문들 받아오기
    @Override
    public MyPageDTO getMyPageData(Long member_id) {

        // 해당 memberId로 회원의 이메일을 가져와 설정 return 에 쓰인다
        Member member = serviceInjection.findById(member_id);

        List<Post> postList = serviceInjection.findAllByMemberId(member_id);

        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : postList) {
            Image image = serviceInjection.findByPostImg_Post(post);
            postDTOList.add(assistant.postBuilder(post, image));
        }
        // MyPageDTO 객체를 생성하여 반환
        return assistant.myPageDTOBuilder(member, member_id, postDTOList);
    }

    //내가 올린 코드 불러오기
    @Override
    public List<SaveCodeDTO> getMyCodeData(Long member_id){
        List<SaveCode> saveCodes = serviceInjection.saveCodeFindAllByMemberId(member_id);
        return assistant.saveCodeDTOSBuilder(saveCodes);
    }

    //채택버튼을 눌렀을 떄 saveCode가 만들어져서 테이블이 만들어지는 코드
    @Override
    public void insert(SaveCodeDTO saveCodeDTO){
        serviceInjection.save(assistant.saveCodeBuilder(saveCodeDTO));
    }
}
