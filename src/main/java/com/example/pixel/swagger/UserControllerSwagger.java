package com.example.pixel.swagger;

import com.example.pixel.dto.ErrorResponseDto;
import com.example.pixel.dto.PageResponseDto;
import com.example.pixel.dto.UserResponseDto;
import com.example.pixel.dto.UserSearchFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User Search", description = "Operations for searching users with filters and pagination")
public interface UserControllerSwagger {

    @Operation(summary = "Search for users (authentication required)",
            description = "Returns a paginated list of users based on the given filter criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "JWT")
    ResponseEntity<PageResponseDto<UserResponseDto>> searchUsers(
            @RequestBody(description = "Filter criteria for searching users")
            UserSearchFilterDto filter,

            @Parameter(description = "Page number (zero-based index)", example = "0")
            int page,

            @Parameter(description = "Page size", example = "10")
            int size
    );
}

