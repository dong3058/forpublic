package com.roulette.roulette.reply.repository;

import com.roulette.roulette.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostPostId(Long postId);
}
