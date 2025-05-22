package com.example.pixel.swagger;

import com.example.pixel.dto.ErrorResponseDto;
import com.example.pixel.dto.PhoneDataRequestDto;
import com.example.pixel.dto.PhoneDataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Phone Data", description = "Operations for managing user's phone data")
public interface PhoneDataControllerSwagger {

    @Operation(summary = "Create phone data (authentication required)",
            description = "Creates a new phone data entry associated with the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Phone data successfully created",
                    content = @Content(schema = @Schema(implementation = PhoneDataResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<PhoneDataResponseDto> create(@Valid @RequestBody PhoneDataRequestDto phoneDataRequestDto);

    @Operation(summary = "Update phone data by ID (authentication required)",
            description = "Updates an existing phone data entry by its ID for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Phone data successfully updated",
                    content = @Content(schema = @Schema(implementation = PhoneDataResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: attempting to update someone else's data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Phone data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<PhoneDataResponseDto> update(@PathVariable Long id,
                                                @Valid @RequestBody PhoneDataRequestDto phoneDataRequestDto);

    @Operation(summary = "Delete phone data by ID (authentication required)",
            description = "Deletes a phone data entry by its ID for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Phone data successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden: attempting to delete someone else's data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Phone data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
