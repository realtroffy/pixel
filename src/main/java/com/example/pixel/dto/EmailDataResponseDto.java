package com.example.pixel.dto;

public record EmailDataResponseDto(
        long emailId,
        long userId,
        String email) {
}
