package com.roulette.roulette.myPage.repository;

import com.roulette.roulette.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyMemberRepository extends JpaRepository<Member, Long> {
}

