package com.msgapp.configs;

import com.msgapp.helpers.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public JwtHandshakeInterceptor() {
        System.out.println(">>> JwtHandshakeInterceptor initialized <<<");
    }
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        String token = request.getHeaders().getFirst("Authorization");
//        System.out.println("token in beforehandshake : "+token);
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            try {
//                String username = jwtUtil.extractUsername(token);
//                System.out.println("token in try : "+token);
//                System.out.println("username in try : "+username);
//
//                attributes.put("username", username);
//                return true;
//            } catch (Exception e) {
//                System.out.println("Invalid token: " + e.getMessage());
//            }
//        }
        //second
        String query = request.getURI().getQuery();
        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6);
            try {
                String username = jwtUtil.extractUsername(token);
                attributes.put("username", username);
                return true;
            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        return false; // reject handshake

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
