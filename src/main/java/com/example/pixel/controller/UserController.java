package com.example.pixel.controller;

import com.example.pixel.dto.PageResponseDto;
import com.example.pixel.dto.UserResponseDto;
import com.example.pixel.dto.UserSearchFilterDto;
import com.example.pixel.service.UserService;
import com.example.pixel.swagger.UserControllerSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerSwagger {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> searchUsers(
            @RequestBody UserSearchFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(userService.searchUsers(filter, page, size));
    }
}
