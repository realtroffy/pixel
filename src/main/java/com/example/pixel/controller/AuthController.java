package com.example.pixel.controller;

import com.example.pixel.config.secutiry.JwtService;
import com.example.pixel.dto.AuthRequestDto;
import com.example.pixel.dto.AuthResponseDto;
import com.example.pixel.swagger.AuthControllerSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerSwagger {

    private final JwtService jwtService;
    private final UserDetailsService emailPhoneUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        UserDetails userDetails = emailPhoneUserDetailsService.loadUserByUsername(request.username());
        String jwtToken = jwtService.createToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDto(jwtToken));
    }
}
