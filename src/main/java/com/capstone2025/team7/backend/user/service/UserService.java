package com.capstone2025.team7.backend.user.service;

import com.capstone2025.team7.backend.exception.BusinessLogicException;
import com.capstone2025.team7.backend.exception.ExceptionCode;
import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.entity.UserAvailableDay;
import com.capstone2025.team7.backend.user.repository.UserAvailableDayRepository;
import com.capstone2025.team7.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAvailableDayRepository availableDayRepository;


    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new BusinessLogicException(ExceptionCode.USER_EXISTS);
                });
        return userRepository.save(user);
    }

    /**
     * 사용자 생성과 함께 가능한 요일들 저장
     */
    public User createUserWithAvailableDays(UserDto.Post request) {
        // 1. 사용자 먼저 저장
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // ... 기타 필드 설정

        User savedUser = userRepository.save(user);

        // 2. 가능한 요일들 저장
        if (request.getAvailableDays() != null && !request.getAvailableDays().isEmpty()) {
            saveUserAvailableDays(savedUser, request.getAvailableDays());
        }

        return savedUser;
    }

    public User updateUser(User user) {
        User findUser = findVerifiedUser(user.getUserId());

        Optional.ofNullable(user.getNickname())
                .ifPresent(findUser::setNickname);
        Optional.ofNullable(user.getPassword())
                .ifPresent(findUser::setPassword);
        Optional.ofNullable(user.getProfileImage())
                .ifPresent(findUser::setProfileImage);

        return userRepository.save(findUser);
    }

    public User findUser(long userId) {
        return findVerifiedUser(userId);
    }

    public void deleteUser(long userId) {
        User findUser = findVerifiedUser(userId);
        userRepository.delete(findUser);
    }

    public User findVerifiedUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    private void saveUserAvailableDays(User user, List<String> availableDays) {
        List<UserAvailableDay> userAvailableDays = availableDays.stream()
                .map(day -> {
                    try {
                        UserAvailableDay.DayOfWeek dayOfWeek = UserAvailableDay.DayOfWeek.valueOf(day.toUpperCase());
                        return new UserAvailableDay(user, dayOfWeek);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("잘못된 요일 형식: " + day);
                    }
                })
                .collect(Collectors.toList());

        availableDayRepository.saveAll(userAvailableDays);
    }
}
