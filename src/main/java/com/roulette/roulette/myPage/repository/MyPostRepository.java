package com.roulette.roulette.myPage.repository;

import com.roulette.roulette.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyPostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.member.memberId = :memberId")
    List<Post> findAllByMemberId(Long memberId);
}