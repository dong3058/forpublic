package com.roulette.roulette.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "save_code")
@EntityListeners(AuditingEntityListener.class)
public class SaveCode {

    @Id @GeneratedValue
    @Column(name = "save_code_id")
    private Long saveCodeId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private Code code;

    @Builder
    public SaveCode(Long saveCodeId, Member member, Code code) {
        this.saveCodeId = saveCodeId;
        this.member = member;
        this.code = code;
    }
}
