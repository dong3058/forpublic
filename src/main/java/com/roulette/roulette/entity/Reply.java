package com.roulette.roulette.entity;

import com.roulette.roulette.auditing.Period;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reply")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Reply extends Period {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "code_id")
    private Code code;

    @Builder
    public Reply(Long replyId, Post post, Member member, Code code) {
        this.replyId = replyId;
        this.post = post;
        this.member = member;
        this.code = code;
    }

    public void add(Code code){
        this.code = code;
    }

}
