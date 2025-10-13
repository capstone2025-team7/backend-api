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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@Validated
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final static String USER_DEFAULT_URL = "/api/users";
    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody UserDto.Post requestBody) {
        User user = userService.createUserWithAvailableDays(requestBody);

        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getUserId());

        List<String> availableDays = userService.getUserAvailableDays(user.getUserId());
        List<String> availableDaysKorean = userService.getUserAvailableDaysInKorean(user.getUserId());

        UserDto.UserResponse response = UserDto.UserResponse.from(user, availableDays, availableDaysKorean);

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @PatchMapping("/{user-id}")
    public ResponseEntity updateUser(@PathVariable("user-id") @Positive long userId, @Valid @RequestBody UserDto.Patch requestBody) {
        requestBody.setUserId(userId);
        User user = userService.updateUser(userMapper.userPatchToUser(requestBody));

        return new ResponseEntity<>(userMapper.userToUserResponse(user), HttpStatus.OK);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @GetMapping("/{user-id}")
    public ResponseEntity getUser(@PathVariable("user-id") @Positive long userId) {
        User user = userService.findUser(userId);
        return new ResponseEntity<>(userMapper.userToUserResponse(user), HttpStatus.OK);
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping("/{user-id}")
    public ResponseEntity deleteUser(@PathVariable("user-id") @Positive long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 사용자의 가능 요일 조회
     */
    @Operation(summary = "사용자 가능 요일 조회", description = "사용자의 가능한 요일을 검색합니다.")
    @GetMapping("/{userId}/available-days")
    public ResponseEntity<Map<String, Object>> getUserAvailableDays(@PathVariable Long userId) {
        List<String> availableDays = userService.getUserAvailableDays(userId);
        List<String> availableDaysKorean = userService.getUserAvailableDaysInKorean(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("availableDays", availableDays);
        response.put("availableDaysKorean", availableDaysKorean);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자의 가능 요일 수정
     */
    @Operation(summary = "사용자 가능 요일 수정", description = "사용자의 가능한 요일을 수정합니다.")
    @PutMapping("/{userId}/available-days")
    public ResponseEntity<String> updateUserAvailableDays(
            @PathVariable Long userId,
            @RequestBody UserDto.AvailableDaysUpdateRequest request) {

        userService.updateUserAvailableDays(userId, request.getAvailableDays());
        return ResponseEntity.ok("가능 요일이 성공적으로 업데이트되었습니다.");
    }

    /**
     * 특정 요일에 가능한 사용자들 조회
     */
    @Operation(summary = "특정 요일 가능 사용자들 조회", description = "특정 요일에 가능한 사용자들을 조회합니다.")
    @GetMapping("/available-on-days")
    public ResponseEntity<List<UserDto.UserResponse>> getUsersAvailableOnDays(
            @RequestParam List<String> days) {

        List<User> users = userService.getUsersAvailableOnDays(days);

        List<UserDto.UserResponse> responses = users.stream()
                .map(user -> {
                    List<String> availableDays = userService.getUserAvailableDays(user.getUserId());
                    List<String> availableDaysKorean = userService.getUserAvailableDaysInKorean(user.getUserId());
                    return UserDto.UserResponse.from(user, availableDays, availableDaysKorean);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자가 특정 요일에 가능한지 확인
     */
    @Operation(summary = "특정 요일 가능 사용자 조회", description = "특정 요일에 가능한 사용자를 조회합니다.")
    @GetMapping("/{userId}/available-on/{day}")
    public ResponseEntity<Map<String, Boolean>> checkUserAvailableOnDay(
            @PathVariable Long userId,
            @PathVariable String day) {

        boolean isAvailable = userService.isUserAvailableOnDay(userId, day);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", isAvailable);

        return ResponseEntity.ok(response);
    }
}
