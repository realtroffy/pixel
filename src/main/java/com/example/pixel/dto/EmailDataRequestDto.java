package com.example.pixel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailDataRequestDto(

        @Schema(description = "User email address", example = "user@example.com")
        @NotBlank(message = "Email must not be blank")
        @Pattern(
                regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                message = "Invalid email address"
        )
        String email) {
}
