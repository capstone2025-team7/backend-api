package com.capstone2025.team7.backend.user.controller;

import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.mapper.UserMapper;
import com.capstone2025.team7.backend.user.service.UserService;
import com.capstone2025.team7.backend.utils.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Validated
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final static String USER_DEFAULT_URL = "/users";
    private final UserService service;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody UserDto.Post requestBody) {
        User user = userMapper.userPostToUser(requestBody);
        service.createUser(user);
        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getUserId());
        return ResponseEntity.created(location).build();
    }
}
