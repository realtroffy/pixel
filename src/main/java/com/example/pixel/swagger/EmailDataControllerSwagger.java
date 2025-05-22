package com.example.pixel.swagger;

import com.example.pixel.dto.EmailDataRequestDto;
import com.example.pixel.dto.EmailDataResponseDto;
import com.example.pixel.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Tag(name = "Email Data", description = "Operations for managing user's email data")
public interface EmailDataControllerSwagger {

    @Operation(summary = "Create email data (authentication required)",
            description = "Creates a new email data entry associated with the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Email data successfully created",
                    content = @Content(schema = @Schema(implementation = EmailDataResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<EmailDataResponseDto> create(@Valid @RequestBody EmailDataRequestDto emailDataRequestDto);

    @Operation(summary = "Update email data by ID (authentication required)",
            description = "Updates an existing email data entry by its ID for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Email data successfully updated",
                    content = @Content(schema = @Schema(implementation = EmailDataResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: attempting to update someone else's data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Email data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<EmailDataResponseDto> update(@PathVariable Long id,
                                                @Valid @RequestBody EmailDataRequestDto emailDataRequestDto);

    @Operation(summary = "Delete email data by ID (authentication required)",
            description = "Deletes an email data entry by its ID for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email data successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden: attempting to delete someone else's data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Email data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
