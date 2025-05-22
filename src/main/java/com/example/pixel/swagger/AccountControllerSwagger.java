package com.example.pixel.swagger;

import com.example.pixel.dto.ErrorResponseDto;
import com.example.pixel.dto.TransferRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Account Operations", description = "Operations for managing account transfers")
public interface AccountControllerSwagger {

    @Operation(
            summary = "Transfer money between accounts (authentication required)",
            description = "Transfers specified amount from authenticated user's account to another account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, insufficient funds or self-transfer attempt",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "423",
                    description = "Account is locked",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<String> transferMoney(Authentication authentication,
            @RequestBody(
                    description = "Transfer details including recipient ID and amount",
                    content = @Content(schema = @Schema(implementation = TransferRequestDto.class))
            )
            TransferRequestDto request
    );
}