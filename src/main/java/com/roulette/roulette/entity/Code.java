package com.roulette.roulette.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "code")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
public class Code {
    @Id@GeneratedValue
    @Column(name = "code_id")
    private Long codeId;

    @Column(name = "html_code_url",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String htmlCodeUrl;

    @Column(name = "css_code_url",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String cssCodeUrl;

    @Column(name = "js_code_url",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String jsCodeUrl;

    @OneToOne(mappedBy = "code")
    private Reply reply;

    @Builder
    public Code(Long codeId, String htmlCodeUrl, String cssCodeUrl, String jsCodeUrl, Reply reply) {
        this.codeId = codeId;
        this.htmlCodeUrl = htmlCodeUrl;
        this.cssCodeUrl = cssCodeUrl;
        this.jsCodeUrl = jsCodeUrl;
        this.reply = reply;
    }
}
