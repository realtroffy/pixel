package com.example.pixel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneDataRequestDto(
        @NotBlank(message = "Phone number must not be blank")
         @Pattern(regexp = "^7\\d{10}$",
                 message = "Phone number must start with '7' and contain exactly 11 digits")
         String phone) {
}
