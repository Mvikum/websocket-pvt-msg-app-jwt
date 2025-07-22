package com.msgapp.configs;

import com.msgapp.helpers.utils.JwtUtil;
import com.msgapp.model.UserCredential;
import com.msgapp.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.extractUsername(token);
                UserCredential user = userCredentialRepository.findByUserName(username);

                if (user == null || !token.equals(user.getJwt()) || !jwtUtil.isTokenValid(token)) {
                    throw new IllegalArgumentException("Invalid or expired token");
                }

                accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
            } else {
                throw new IllegalArgumentException("Missing or invalid token");
            }
        }
        return message;
    }
}
