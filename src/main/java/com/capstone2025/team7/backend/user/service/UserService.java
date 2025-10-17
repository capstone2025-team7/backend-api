package com.capstone2025.team7.backend.user.service;

import com.capstone2025.team7.backend.auth.utils.CustomAuthorityUtils;
import com.capstone2025.team7.backend.exception.BusinessLogicException;
import com.capstone2025.team7.backend.exception.ExceptionCode;
import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.entity.UserAvailableDay;
import com.capstone2025.team7.backend.user.repository.UserAvailableDayRepository;
import com.capstone2025.team7.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
    private final CustomAuthorityUtils authorityUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성과 함께 가능한 요일들 저장
     */
    public User createUserWithAvailableDays(UserDto.Post request) {
        // 중복 체크
        verifiedExistsUser(request.getEmail(), request.getNickname());

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 권한 부여
        List<String> roles = authorityUtils.createRoles(request.getEmail());

        User user = new User();
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPassword(encryptedPassword);
        user.setProfileImage(request.getProfileImage());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setRoles(roles);
        user.setLocation(request.getLocation());

        User savedUser = userRepository.save(user);

        // 2. 가능한 요일들 저장
        if (request.getAvailableDays() != null && !request.getAvailableDays().isEmpty()) {
            saveUserAvailableDays(savedUser, request.getAvailableDays());
        }

        return savedUser;
    }

    public User updateUser(User user) {
        User findUser = findVerifiedUser(user.getEmail());

        Optional.ofNullable(user.getNickname())
                .ifPresent(findUser::setNickname);

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        Optional.ofNullable(user.getPassword())
                .ifPresent(password -> findUser.setPassword(encryptedPassword));

        Optional.ofNullable(user.getProfileImage())
                .ifPresent(findUser::setProfileImage);

        return userRepository.save(findUser);
    }

    public User findUser(String email) {
        return findVerifiedUser(email);
    }

    public void deleteUser(String email) {
        User findUser = findVerifiedUser(email);
        userRepository.delete(findUser);
    }

    public User findVerifiedUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    public User findVerifiedUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    /**
     * 사용자의 가능한 요일들 업데이트
     */
    public void updateUserAvailableDays(String email, List<String> availableDays) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 기존 가능 요일들 삭제
        availableDayRepository.deleteByemail(email);

        // 새로운 가능 요일들 저장
        if (availableDays != null && !availableDays.isEmpty()) {
            saveUserAvailableDays(user, availableDays);
        }
    }

    /**
     * 가능한 요일들 저장 (내부 메서드)
     */
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

    /**
     * 사용자의 가능한 요일들 조회
     */
    @Transactional(readOnly = true)
    public List<String> getUserAvailableDays(String email) {
        return availableDayRepository.findByUser_Email(email)
                .stream()
                .map(availableDay -> availableDay.getDayOfWeek().name())
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 가능한 요일들 조회 (한글명)
     */
    @Transactional(readOnly = true)
    public List<String> getUserAvailableDaysInKorean(String email) {
        return availableDayRepository.findByUser_Email(email)
                .stream()
                .map(availableDay -> availableDay.getDayOfWeek().getKoreanName())
                .collect(Collectors.toList());
    }

    /**
     * 특정 요일들에 모두 가능한 사용자들 조회
     */
    @Transactional(readOnly = true)
    public List<User> getUsersAvailableOnDays(List<String> days) {
        List<UserAvailableDay.DayOfWeek> dayOfWeeks = days.stream()
                .map(day -> UserAvailableDay.DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toList());

        return availableDayRepository.findUsersAvailableOnAllDays(dayOfWeeks, dayOfWeeks.size());
    }

    /**
     * 사용자가 특정 요일에 가능한지 확인
     */
    @Transactional(readOnly = true)
    public boolean isUserAvailableOnDay(Long userId, String day) {
        try {
            UserAvailableDay.DayOfWeek dayOfWeek = UserAvailableDay.DayOfWeek.valueOf(day.toUpperCase());
            return availableDayRepository.existsByUser_UserIdAndDayOfWeek(userId, dayOfWeek);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void verifiedExistsUser(String email, String nickname) {
        Optional<User> findByEmail = userRepository.findByEmail(email);
        if (findByEmail.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.USER_EXISTS);
        }

        Optional<User> findByNickname = userRepository.findByNickname(nickname);
        if (findByNickname.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_EXISTS);
        }
    }


}
