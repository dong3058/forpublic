package com.roulette.roulette.chatting.controller;

import com.roulette.roulette.chatting.dto.ChatStartRequest;
import com.roulette.roulette.chatting.dto.MessageDTO;
import com.roulette.roulette.chatting.service.MessagingService;
import com.roulette.roulette.entity.Conversation;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {

    @Autowired
    private MessagingService messagingService;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(MessagingService messagingService, SimpMessagingTemplate messagingTemplate) {
        this.messagingService = messagingService;
        this.messagingTemplate = messagingTemplate;
    }

    // memberId를 통해 특정 사용자의 모든 대화 목록을 보여주는 페이지를 반환합니다.
    @GetMapping("/chat/{memberId}")
    public String getAllChatsForUser(@PathVariable Long memberId, Model model) {
        model.addAttribute("conversations", messagingService.getAllConversationsByMemberId(memberId));
        model.addAttribute("memberId", memberId);
        return "chat";
    }

    // 선택된 대화에 대한 대화창을 보여주는 페이지를 반환합니다.
    @GetMapping("/chat/{memberId}/{conversationId}")
    public String getConversation(@PathVariable Long memberId, @PathVariable Long conversationId, Model model) {
        Conversation selectedConversation = messagingService.getConversation(conversationId);
        if (selectedConversation != null && selectedConversation.getMembers().stream().anyMatch(member -> member.getMemberId().equals(memberId))) {
            model.addAttribute("conversations", messagingService.getAllConversationsByMemberId(memberId));
            model.addAttribute("selectedConversation", selectedConversation);
            model.addAttribute("messages", messagingService.getMessagesByConversation(conversationId));
            model.addAttribute("memberId", memberId);
            boolean isSelected = selectedConversation.getMembers().stream()
                    .anyMatch(member -> member.getMemberId().equals(memberId));
            model.addAttribute("isSelected", isSelected);
            return "conversation";
        }
        return "redirect:/chat/" + memberId;
    }

    // 클라이언트로부터 메시지를 받아 다른 클라이언트에게 전송합니다.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/messages")
    @Transactional
    public Message sendMessage(MessageDTO messageDTO) {
        Conversation conversation = messagingService.getConversation(messageDTO.getConversationId());
        Member sender = messagingService.getMember(messageDTO.getSenderId()); // getMember 메서드는 회원의 ID를 기반으로 회원 정보를 조회하는 메서드입니다.

        if (conversation == null || sender == null) {
            throw new IllegalStateException("No conversation or sender found with the given ID.");
        }

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setConversation(conversation);
        message.setSender(sender);
        message.setTimestamp(System.currentTimeMillis());
        // timestamp 설정 등 필요한 추가 설정

        // 메시지 저장
        Message savedMessage = messagingService.sendMessage(conversation.getConversationId(), message);

        String destination = "/topic/messages/" + message.getConversation().getConversationId();
        // 구독자에게 메시지 전송
        messagingTemplate.convertAndSend(destination, message);

        return savedMessage;
    }

    @PostMapping("/chat/start")
    public ResponseEntity<String> startChat(@RequestBody ChatStartRequest request){
        messagingService.startChat(request.getSender(), request.getReceiver(), request.getMessage());
        return ResponseEntity.ok("대화가 시작되었습니다.");
    }

//    // 웹소켓 구독자에게 메시지를 보냅니다. (예시: 새 메시지 알림)
//    public void sendMessagesToSubscribers(Message message) {
//        Message savedMessage = messagingService.sendMessage(message.getConversation().getConversationId(), message);
//        messagingTemplate.convertAndSend("/topic/messages", savedMessage);
//    }
}
