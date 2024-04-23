package com.roulette.roulette.chatting.runner;

import com.roulette.roulette.chatting.repository.ConversationRepository;
import com.roulette.roulette.chatting.repository.MessageRepository;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import org.springframework.boot.CommandLineRunner;
import com.roulette.roulette.entity.Conversation;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class ChatAppRunner implements CommandLineRunner {

    private final ConversationRepository conversationRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final MessageRepository messageRepository;

    public ChatAppRunner(ConversationRepository conversationRepository,
                         MemberJpaRepository memberJpaRepository,
                         MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create dummy members
        Member alice = new Member();
        alice.setName("Alice Johnson");
        alice.setEmail("alice@example.com");

        Member bob = new Member();
        bob.setName("Bob Smith");
        bob.setEmail("bob@example.com");

        Member steve = new Member();
        steve.setName("Steve Johnson");
        steve.setEmail("steve@example.com");

        Member kei = new Member();
        kei.setName("Kei Smith");
        kei.setEmail("kei@example.com");

        memberJpaRepository.saveAll(Arrays.asList(alice, bob, steve, kei));

        // Create a conversation between Alice and Bob
        Conversation conversationAB = new Conversation();
        conversationAB.setMembers(new HashSet<>(Arrays.asList(alice, bob)));
        conversationRepository.save(conversationAB);

        // Create a conversation between Alice and Steve
        Conversation conversationAS = new Conversation();
        conversationAS.setMembers(new HashSet<>(Arrays.asList(alice, steve)));
        conversationRepository.save(conversationAS);

        // Create a conversation between Alice and Kei
        Conversation conversationAK = new Conversation();
        conversationAK.setMembers(new HashSet<>(Arrays.asList(alice, kei)));
        conversationRepository.save(conversationAK);

        // Create dummy messages for each conversation
        // Messages for Alice and Bob
        Message message1 = new Message();
        message1.setConversation(conversationAB);
        message1.setSender(alice);
        message1.setContent("안녕하세요, Bob!");
        message1.setTimestamp(System.currentTimeMillis());

        Message message2 = new Message();
        message2.setConversation(conversationAB);
        message2.setSender(bob);
        message2.setContent("안녕 Alice, 잘 지내?");
        message2.setTimestamp(System.currentTimeMillis());

        // Messages for Alice and Steve
        Message message3 = new Message();
        message3.setConversation(conversationAS);
        message3.setSender(alice);
        message3.setContent("Hi Steve, how are you?");
        message3.setTimestamp(System.currentTimeMillis());

        Message message4 = new Message();
        message4.setConversation(conversationAS);
        message4.setSender(steve);
        message4.setContent("Hello Alice, I'm good thanks!");
        message4.setTimestamp(System.currentTimeMillis());

        // Messages for Alice and Kei
        Message message5 = new Message();
        message5.setConversation(conversationAK);
        message5.setSender(alice);
        message5.setContent("Hey Kei, what's up?");
        message5.setTimestamp(System.currentTimeMillis());

        Message message6 = new Message();
        message6.setConversation(conversationAK);
        message6.setSender(kei);
        message6.setContent("Hi Alice, doing well here!");
        message6.setTimestamp(System.currentTimeMillis());

        // Save all messages
        messageRepository.saveAll(Arrays.asList(message1, message2, message3, message4, message5, message6));
    }
}
