package com.example.pixel.util;

import com.example.pixel.config.secutiry.JwtService;
import com.example.pixel.exception.UserNotFoundException;
import com.example.pixel.model.User;
import com.example.pixel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Authenticator {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public User findByUserIdFromJwtToken() {
        String jwtToken = TokenExtractorUtil.getTokenFromRequest();
        Long userId = jwtService.extractUserId(jwtToken);
        return userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found by id from jwt token"));
    }
}
