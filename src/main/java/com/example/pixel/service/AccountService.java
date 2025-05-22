package com.example.pixel.service;

import com.example.pixel.dto.TransferRequestDto;
import org.springframework.security.core.Authentication;

public interface AccountService {

    void transfer(Authentication authentication, TransferRequestDto transferRequestDto);
}
