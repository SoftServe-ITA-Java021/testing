package com.kh021j.travelwithpleasurehub.controller;

import com.kh021j.travelwithpleasurehub.event.LoginEvent;
import com.kh021j.travelwithpleasurehub.event.ParticipantRepository;
import com.kh021j.travelwithpleasurehub.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collection;

/**
 * Created by ${JDEEK} on ${11.11.2018}.
 */
@Controller
public class ChatController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/chat.participants")
    public Collection<LoginEvent> retrieveParticipants(){
        return participantRepository.getActiveSessions().values();
    }

    @MessageMapping("/chat.message")
    public User filterMessage(@Payload User message, Principal principal){
        message.setUsername(principal.getName());

        return message;
    }

    @MessageMapping("/chat.private.{username}")
    public void filterPrivateMessage(@Payload User message, @DestinationVariable("username") String username, Principal principal){
        message.setUsername(principal.getName());
            if (!message.getUsername().equals(username)){
                simpMessagingTemplate.convertAndSend("/user/"+username+"/exchange/amq.direct/chat.message",message);
            }
    }

    public void setParticipantRepository(ParticipantRepository participantRepository){
        this.participantRepository = participantRepository;
    }
}
