package com.msgapp.controller;

import com.msgapp.dtos.OutputMessage;
import com.msgapp.dtos.SecureMessage;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class SecureChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public SecureChatController(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

//    @MessageMapping("/secure")
//    public void sendSpecific(@Payload SecureMessage msg,
//                             Principal user,
//                             @Header("simpSessionId") String sessionId) throws Exception {
//        OutputMessage outputMessage = new OutputMessage(
//                msg.getFrom(),
//                msg.getText(),
//                new SimpleDateFormat("HH:mm").format(new Date())
//        );
//        simpMessagingTemplate.convertAndSendToUser(
//                msg.getTo(),
//                "/queue/chat",
//                outputMessage
//        );
//    }
//    @PreAuthorize("#username == authentication.name")

    @MessageMapping("/secure")
    public void sendSpecific(@Payload SecureMessage msg,
                             @Header("simpSessionId") String sessionId) throws Exception {
        OutputMessage outputMessage = new OutputMessage(
                msg.getFrom(),
                msg.getText(),
                new SimpleDateFormat("HH:mm").format(new Date())
        );

        // Send to specific user
        simpMessagingTemplate.convertAndSendToUser(
                msg.getTo(),
                "/queue/chat",
                outputMessage

        );
        System.out.println(outputMessage);
        System.out.println("msg.to = " + msg.getTo());
        System.out.println("Connected principal: " + simpMessagingTemplate.getUserDestinationPrefix());

    }
}
