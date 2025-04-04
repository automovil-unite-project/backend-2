package com.automovil_unite.backend.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.automovil_unite.backend.application.dto.LoginRequestDto;
import com.automovil_unite.backend.application.dto.TokenResponseDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws AuthenticationException {
        try {
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getEmail(), 
                    loginRequestDto.getPassword(), 
                    Collections.emptyList());
            
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           FilterChain chain, Authentication authResult) throws IOException {
        String token = tokenProvider.generateToken(authResult);
        
        TokenResponseDto tokenResponse = TokenResponseDto.builder()
                .token(token)
                .tokenType(jwtProperties.getTokenPrefix())
                .expiresIn(jwtProperties.getExpirationTime() / 1000)
                .build();
        
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}
