package com.example.pixel.service;

import com.example.pixel.dto.PageResponseDto;
import com.example.pixel.dto.UserResponseDto;
import com.example.pixel.dto.UserSearchFilterDto;

public interface UserService {

    PageResponseDto<UserResponseDto> searchUsers(UserSearchFilterDto filter, int page, int size);
}
