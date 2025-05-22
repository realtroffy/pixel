package com.example.pixel.mapper;

import com.example.pixel.dto.UserResponseDto;
import com.example.pixel.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserMapper {

    public static UserResponseDto toUserResponseDto(User user,
                                                    Map<Long, List<String>> emailsMap,
                                                    Map<Long, List<String>> phonesMap) {
        return new UserResponseDto(
                user.getId(),
                user.getDateOfBirth(),
                user.getName(),
                emailsMap.getOrDefault(user.getId(), Collections.emptyList()),
                phonesMap.getOrDefault(user.getId(), Collections.emptyList()),
                user.getAccount().getInitialBalance(),
                user.getAccount().getCurrentBalance());
    }
}
