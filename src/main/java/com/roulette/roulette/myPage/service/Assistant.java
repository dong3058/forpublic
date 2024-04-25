package com.roulette.roulette.myPage.service;

import com.roulette.roulette.auditing.dto.mypage.MyPageDTO;
import com.roulette.roulette.auditing.dto.mypage.PostDTO;
import com.roulette.roulette.auditing.dto.mypage.SaveCodeDTO;
import com.roulette.roulette.entity.Image;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.entity.SaveCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Assistant {

    public String getEmail(Member member){
        String email;
        if (member != null)
            email = member.getEmail();
        else
            email = "No email";
        return email;
    }

    public PostDTO postBuilder(Post post, Image image){
        PostDTO postDTO = PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .createdTime(post.getCreateTime())
                .imgSrc(image.getImgUrl())
                .build();
        return postDTO;
    }

    public MyPageDTO myPageDTOBuilder(Member member, Long member_id, List<PostDTO> postDTOList){
        MyPageDTO myPageDTO = MyPageDTO.builder()
                .email(getEmail(member))
                .postList(postDTOList)
                .memberId(member_id)
                .build();
        return myPageDTO;
    }

    public List<SaveCodeDTO> saveCodeDTOSBuilder(List<SaveCode> saveCodes){
        List<SaveCodeDTO> saveCodeDTOS = new ArrayList<>();
        for (SaveCode saveCode : saveCodes) {
            SaveCodeDTO saveCodeDTO = SaveCodeDTO.builder()
                    .code(saveCode.getCode())
                    .member(saveCode.getMember())
                    .saveCodeId(saveCode.getSaveCodeId())
                    .build();
            saveCodeDTOS.add(saveCodeDTO);
        }
        return saveCodeDTOS;
    }

    public SaveCode saveCodeBuilder(SaveCodeDTO saveCodeDTO) {
        SaveCode saveCode = SaveCode.builder()
                .code(saveCodeDTO.getCode())
                .member(saveCodeDTO.getMember())
                .saveCodeId(saveCodeDTO.getSaveCodeId())
                .build();
        return saveCode;
    }

}
