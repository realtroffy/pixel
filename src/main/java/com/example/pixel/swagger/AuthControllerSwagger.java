package com.example.pixel.swagger;

import com.example.pixel.dto.AuthRequestDto;
import com.example.pixel.dto.AuthResponseDto;
import com.example.pixel.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "User authentication methods")
public interface AuthControllerSwagger {

    @Operation(
            summary = "User authentication",
            description = "Accepts username and password, and returns a JWT token upon successful authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody(
                    required = true,
                    description = "Username and password",
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
            ) AuthRequestDto request
    );
}
