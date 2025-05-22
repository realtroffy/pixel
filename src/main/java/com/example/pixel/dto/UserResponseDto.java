package com.example.pixel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UserResponseDto(
        long userId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        LocalDate dateOfBirth,
        String name,
        List<String> emails,
        List<String> phones,
        BigDecimal initialBalance,
        BigDecimal currentBalance) {
}
