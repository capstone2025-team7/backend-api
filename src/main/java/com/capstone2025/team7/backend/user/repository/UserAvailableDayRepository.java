package com.capstone2025.team7.backend.user.repository;

import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.entity.UserAvailableDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAvailableDayRepository extends JpaRepository<UserAvailableDay, Long> {

    // 특정 사용자의 가능한 요일들 조회
    List<UserAvailableDay> findByUser_UserId(Long userId);

    // 특정 사용자의 특정 요일 조회
    UserAvailableDay findByUser_UserIdAndDayOfWeek(Long userId, UserAvailableDay.DayOfWeek dayOfWeek);

    // 특정 요일에 가능한 모든 사용자들 조회
    List<UserAvailableDay> findByDayOfWeek(UserAvailableDay.DayOfWeek dayOfWeek);

    // 특정 사용자의 가능한 요일들 삭제
    @Modifying
    @Query("DELETE FROM UserAvailableDay uad WHERE uad.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 특정 사용자가 특정 요일에 가능한지 확인
    boolean existsByUser_UserIdAndDayOfWeek(Long userId, UserAvailableDay.DayOfWeek dayOfWeek);

    // 여러 요일에 모두 가능한 사용자들 조회
    @Query("SELECT DISTINCT uad.user FROM UserAvailableDay uad " +
            "WHERE uad.dayOfWeek IN :days " +
            "GROUP BY uad.user " +
            "HAVING COUNT(DISTINCT uad.dayOfWeek) = :dayCount")
    List<User> findUsersAvailableOnAllDays(@Param("days") List<UserAvailableDay.DayOfWeek> days,
                                           @Param("dayCount") long dayCount);
}