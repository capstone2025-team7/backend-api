package com.capstone2025.team7.backend.club.repository;


import com.capstone2025.team7.backend.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    // ClubRepository에 추가
    List<Club> findByIsActiveTrue();
    List<Club> findByIsActiveFalse();

    /**
     * 부모 클럽 이름, 활동 요일, 그리고 활성화 상태를 기준으로 클럽을 찾습니다.
     * @return Optional<Club>
     */
    Optional<Club> findByParentClubNameAndActivityDayAndIsActive(String parentClubName, DayOfWeek activityDay, Boolean isActive);

    /**
     * 부모 클럽 이름과 활동 요일에 해당하는 활성화된 동호회의 수를 셉니다.
     * @return long
     */
    long countByParentClubNameAndActivityDayAndIsActive(String parentClubName, DayOfWeek activityDay, Boolean isActive);

}
