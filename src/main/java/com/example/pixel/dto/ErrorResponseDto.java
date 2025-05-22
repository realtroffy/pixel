package com.example.pixel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDto(
        @Schema(description = "Текст ошибки")
        String detail) {
}