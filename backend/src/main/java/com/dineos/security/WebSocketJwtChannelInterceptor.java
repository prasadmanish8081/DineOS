package com.dineos.security;

import com.dineos.exception.WebSocketAuthException;
import com.dineos.util.JwtTokenUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public WebSocketJwtChannelInterceptor(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            String token = extractToken(accessor.getNativeHeader("Authorization"));
            if (token == null) {
                throw new WebSocketAuthException("Missing Authorization token");
            }

            try {
                String email = jwtTokenUtil.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (!jwtTokenUtil.isTokenValid(token, userDetails)) {
                    throw new WebSocketAuthException("Invalid WebSocket token");
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            } catch (UsernameNotFoundException ex) {
                throw new WebSocketAuthException("User not found");
            } catch (Exception ex) {
                throw new WebSocketAuthException("Invalid WebSocket token");
            }
        }
        return message;
    }

    private String extractToken(List<String> headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        String raw = headers.get(0);
        if (raw == null) {
            return null;
        }
        return raw.startsWith("Bearer ") ? raw.substring(7) : raw;
    }
}
