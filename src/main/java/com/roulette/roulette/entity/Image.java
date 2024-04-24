package com.roulette.roulette.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@Table(name = "image")
@EntityListeners(AuditingEntityListener.class)
@Data
@Entity
public class Image {

    @Id
    @Column(name = "img_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_img_id", nullable = true)
    private ReplyImage replyImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_img_id", nullable = true)
    private PostImage postImg;

    @Column(name = "img_url",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String imgUrl;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "delete_time")
    private LocalDateTime deleteTime;
}
