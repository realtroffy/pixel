package com.example.pixel.service;

import com.example.pixel.dto.EmailDataRequestDto;
import com.example.pixel.dto.EmailDataResponseDto;

public interface EmailDataService {

    EmailDataResponseDto create(EmailDataRequestDto emailDataRequestDto);

    EmailDataResponseDto update(Long emailId, EmailDataRequestDto emailDataRequestDto);

    void delete(Long emailId);
}
