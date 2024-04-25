package com.roulette.roulette.myPage.service;

import com.roulette.roulette.entity.Image;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.entity.SaveCode;
import com.roulette.roulette.myPage.repository.MyMemberRepository;
import com.roulette.roulette.myPage.repository.MyPostRepository;
import com.roulette.roulette.myPage.repository.SaveCodeRepository;
import com.roulette.roulette.post.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceInjection {
    private  final MyMemberRepository myMemberRepository;
    private  final MyPostRepository myPostRepository;
    private final SaveCodeRepository saveCodeRepository;
    private final ImageRepository imageRepository;

    public Member  findById(Long member_id){
        Member member = myMemberRepository.findById(member_id).orElse(null);
        if (member == null)
            return null;
        return member;
    }

    public List<Post> findAllByMemberId(Long member_id){
        return myPostRepository.findAllByMemberId(member_id);
    }

    public Image findByPostImg_Post(Post post){
        return imageRepository.findByPostImg_Post(post).get();
    }

    public List<SaveCode> saveCodeFindAllByMemberId(Long member_id){
        return saveCodeRepository.findAllByMemberId(member_id);
    }

    public void save(SaveCode saveCode) {
        saveCodeRepository.save(saveCode);
    }

}
