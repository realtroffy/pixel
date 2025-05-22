package com.example.pixel.mapper;

import com.example.pixel.dto.PhoneDataResponseDto;
import com.example.pixel.model.PhoneData;

public class PhoneDataMapper {

    public static PhoneDataResponseDto toPhoneDataResponseDto(PhoneData phoneData) {
        return new PhoneDataResponseDto(phoneData.getId(), phoneData.getUser().getId(), phoneData.getPhone());
    }
}
