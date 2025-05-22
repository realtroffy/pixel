package com.example.pixel.controller;

import com.example.pixel.dto.TransferRequestDto;
import com.example.pixel.service.AccountService;
import com.example.pixel.swagger.AccountControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountControllerSwagger {

    private final AccountService accountService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            Authentication authentication,
            @RequestBody @Valid TransferRequestDto request) {

        accountService.transfer(authentication, request);
        return ResponseEntity.ok("Transfer completed successfully");
    }
}
