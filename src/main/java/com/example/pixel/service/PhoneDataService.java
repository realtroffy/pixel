package com.example.pixel.service;

import com.example.pixel.dto.PhoneDataRequestDto;
import com.example.pixel.dto.PhoneDataResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

public interface PhoneDataService {

    PhoneDataResponseDto create(PhoneDataRequestDto phoneDataRequestDto);

    PhoneDataResponseDto update(Long id, PhoneDataRequestDto phoneDataRequestDto);

    void delete(Long id);
}
