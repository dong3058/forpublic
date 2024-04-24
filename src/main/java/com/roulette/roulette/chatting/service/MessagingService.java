package com.roulette.roulette.chatting.service;

import com.roulette.roulette.chatting.repository.ConversationRepository;
import com.roulette.roulette.chatting.repository.MessageRepository;
import com.roulette.roulette.entity.Conversation;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Message;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessagingService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Transactional
    public Conversation createConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    @Transactional
    public Message sendMessage(Long conversationId, Message message) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found with id: " + conversationId));

        // Associate the existing conversation with the message.
        message.setConversation(conversation);

        // Save the message with the associated conversation.
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversation_ConversationId(conversationId);
    }

    // 모든 대화 목록을 반환하는 메소드
    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    // 특정 대화를 반환하는 메소드
    public Conversation getConversation(Long conversationId) {
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);
        if(conversation.isPresent()) {
            return conversation.get();
        } else {
            // 적절한 예외 처리나 대체 로직을 수행합니다.
            throw new RuntimeException("Conversation not found!"); // 이 부분은 실제 응용 프로그램에서는 좀 더 세밀한 예외 처리가 필요합니다.
        }
    }

    // memberId에 따라 대화 목록을 가져오는 메서드
    public List<Conversation> getAllConversationsByMemberId(Long memberId) {
        return conversationRepository.findByMembers_MemberId(memberId);
    }

    public Member getMember(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
    }

    public void startChat(Long senderid, Long receiverid, String context) {
        Member sender = getMember(senderid);
        Member receiver = getMember(receiverid);
        Conversation conversation = new Conversation();
        conversation.setMembers(new HashSet<>(Arrays.asList(sender, receiver)));
        conversationRepository.save(conversation);

        Message startmessage = new Message();
        startmessage.setConversation(conversation);
        startmessage.setSender(sender);
        startmessage.setContent("채팅을 시작합니다.");
        startmessage.setTimestamp(System.currentTimeMillis());

        messageRepository.save(startmessage);

    }
}
