package com.roulette.roulette.chatting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatStartRequest {
    private Long sender;
    private Long receiver;
    private String message;
}
