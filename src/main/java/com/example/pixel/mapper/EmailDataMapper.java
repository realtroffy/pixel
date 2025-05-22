package com.example.pixel.mapper;

import com.example.pixel.dto.EmailDataResponseDto;
import com.example.pixel.model.EmailData;

public class EmailDataMapper {

    public static EmailDataResponseDto toEmailDataResponseDto(EmailData emailData) {
        return new EmailDataResponseDto(emailData.getId(), emailData.getUser().getId(), emailData.getEmail());
    }
}
