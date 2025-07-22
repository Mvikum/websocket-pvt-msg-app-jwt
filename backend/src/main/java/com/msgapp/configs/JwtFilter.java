package com.msgapp.configs;

import com.msgapp.helpers.utils.JwtUtil;
import com.msgapp.model.UserCredential;
import com.msgapp.repository.UserCredentialRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;


@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserCredentialRepository userCredentialRepository;
    public JwtFilter(JwtUtil jwtUtil,UserCredentialRepository userCredentialRepository){
        this.jwtUtil= jwtUtil;
        this.userCredentialRepository=userCredentialRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (request.getParameter("token") != null) {
            token = request.getParameter("token");
        }

        if (token != null) {
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Invalid token, stop here
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenValid(token)) {
                UserCredential user = userCredentialRepository.findByUserName(username);

                if (user != null && token.equals(user.getJwt())) {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getUserName(), user.getPassword(), new ArrayList<>());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return; // Token not matching DB
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Expired or invalid token
            }
        }

        chain.doFilter(request, response);
    }
}
