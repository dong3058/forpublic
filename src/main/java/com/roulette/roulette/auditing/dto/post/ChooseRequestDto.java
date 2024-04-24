package com.roulette.roulette.auditing.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor //All에서 No로 바꿧을때 오류가 발생하지 않음. 원인 분석 필요.
public class ChooseRequestDto {
    private Long postId;
}
