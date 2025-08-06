package com.capstone2025.team7.backend.user.controller;

import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.mapper.UserMapper;
import com.capstone2025.team7.backend.user.service.UserService;
import com.capstone2025.team7.backend.utils.UriCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@Validated
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final static String USER_DEFAULT_URL = "/users";
    private final UserService service;
    private final UserMapper userMapper;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody UserDto.Post requestBody) {
        User user = userMapper.userPostToUser(requestBody);
        service.createUser(user);
        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getUserId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @PatchMapping("/{user-id}")
    public ResponseEntity updateUser(@PathVariable("user-id") @Positive long userId, @Valid @RequestBody UserDto.Patch requestBody) {
        requestBody.setUserId(userId);
        User user = service.updateUser(userMapper.userPatchToUser(requestBody));

        return new ResponseEntity<>(userMapper.userToUserResponse(user), HttpStatus.OK);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @GetMapping("/{user-id}")
    public ResponseEntity getUser(@PathVariable("user-id") @Positive long userId) {
        User user = service.findUser(userId);
        return new ResponseEntity<>(userMapper.userToUserResponse(user), HttpStatus.OK);
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping("/{user-id}")
    public ResponseEntity deleteUser(@PathVariable("user-id") @Positive long userId) {
        service.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
