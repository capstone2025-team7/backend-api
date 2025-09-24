// UserClubRepository.java
package com.capstone2025.team7.backend.userClub.repository;

import com.capstone2025.team7.backend.userClub.entity.UserClub;
import com.capstone2025.team7.backend.userClub.entity.UserClub.UserClubStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserClubRepository extends JpaRepository<UserClub, Long> {

    /**
     * 특정 동호회의 활성 멤버 수 조회
     */
    @Query("SELECT COUNT(uc) FROM UserClub uc WHERE uc.club.clubId = :clubId AND :activeStatus MEMBER OF uc.userClubStatuses")
    Long countActiveMembers(@Param("clubId") Long clubId, @Param("activeStatus") UserClubStatus activeStatus);

    /**
     * 특정 동호회의 모든 멤버 조회
     */
    List<UserClub> findByClub_ClubId(Long clubId);

    /**
     * 특정 사용자가 가입한 모든 동호회 조회
     */
    List<UserClub> findByUser_UserId(Long userId);

    /**
     * 특정 사용자가 특정 동호회에 가입했는지 조회
     */
    Optional<UserClub> findByUser_UserIdAndClub_ClubId(Long userId, Long clubId);

    /**
     * 가입 신청했는지 확인
     */
    boolean existsByUser_UserIdAndClub_ClubId(Long userId, Long clubId);

    /**
     * 특정 동호회의 가입 대기 중인 사용자 수 조회 (최소인원 체크용)
     */
    @Query("SELECT COUNT(uc) FROM UserClub uc WHERE uc.club.clubId = :clubId AND :waitStatus MEMBER OF uc.userClubStatuses")
    Long countPendingMembers(@Param("clubId") Long clubId, @Param("waitStatus") UserClubStatus waitStatus);

    // --- 추가된 메소드 ---

    /**
     * 특정 동호회에서 특정 상태를 가진 모든 사용자 목록 조회
     */
    List<UserClub> findByClub_ClubIdAndUserClubStatuses(Long clubId, UserClubStatus userClubStatus);
}