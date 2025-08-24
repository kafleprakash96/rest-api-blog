package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.JwtResponseDto;
import org.prkguides.blog.dto.LoginRequestDto;
import org.prkguides.blog.dto.UserSummaryDto;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.security.JwtTokenProvider;
import org.prkguides.blog.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()-> new ResourceNotFoundException("User", "username", loginRequest.getUsername()));

            UserSummaryDto userSummaryDto = modelMapper.map(user, UserSummaryDto.class);

            log.info("User {} authenticated successfully", loginRequest.getUsername());

            return JwtResponseDto.builder()
                    .token(jwt)
                    .type("Bearer")
                    .user(userSummaryDto)
                    .build();
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
}
