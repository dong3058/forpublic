package com.roulette.roulette.myPage.repository;

import com.roulette.roulette.entity.SaveCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaveCodeRepository extends JpaRepository<SaveCode, Long> {
    @Query("SELECT s FROM SaveCode s WHERE s.member.memberId = :memberId")
    List<SaveCode> findAllByMemberId(Long memberId);

}
