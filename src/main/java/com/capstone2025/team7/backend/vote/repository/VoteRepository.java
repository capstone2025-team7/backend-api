package com.capstone2025.team7.backend.vote.repository;

import com.capstone2025.team7.backend.vote.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 클럽별 일정 조회
    Page<Vote> findByClubClubId(Long clubId, Pageable pageable);

    // 사용자별 일정 조회
    Page<Vote> findByUserUserId(Long userId, Pageable pageable);

    // 클럽과 사용자로 일정 조회
    Page<Vote> findByClubClubIdAndUserUserId(Long clubId, Long userId, Pageable pageable);

    // 투표 상태별 조회
    Page<Vote> findByVoteStatus(Vote.VoteStatus voteStatus, Pageable pageable);

    // 진행 중인 일정 조회 (현재 시간이 시작일과 종료일 사이이면서 ACTIVE 상태)
    @Query("SELECT v FROM Vote v WHERE v.startDate <= :now AND v.dueDate >= :now AND v.voteStatus = :status")
    Page<Vote> findActiveVotesInProgress(@Param("now") LocalDateTime now,
                                         @Param("status") Vote.VoteStatus status,
                                         Pageable pageable);

    // 특정 기간 내 일정 조회
    @Query("SELECT v FROM Vote v WHERE v.startDate >= :startDate AND v.dueDate <= :endDate")
    Page<Vote> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               Pageable pageable);

    // 클럽의 특정 기간 일정 조회
    @Query("SELECT v FROM Vote v WHERE v.club.clubId = :clubId AND v.startDate >= :startDate AND v.dueDate <= :endDate")
    Page<Vote> findByClubIdAndDateRange(@Param("clubId") Long clubId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    // 만료된 일정 조회 (종료일이 지난 것들)
    Page<Vote> findByDueDateBefore(LocalDateTime dueDate, Pageable pageable);

    // 곧 시작할 일정 조회
    @Query("SELECT v FROM Vote v WHERE v.startDate BETWEEN :now AND :futureTime AND v.voteStatus = :status ORDER BY v.startDate ASC")
    List<Vote> findUpcomingVotes(@Param("now") LocalDateTime now,
                                 @Param("futureTime") LocalDateTime futureTime,
                                 @Param("status") Vote.VoteStatus status);

    // 클럽의 활성 일정 수 조회
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.club.clubId = :clubId AND v.voteStatus = :status")
    long countActiveVotesByClub(@Param("clubId") Long clubId, @Param("status") Vote.VoteStatus status);

    // 사용자의 활성 일정 수 조회
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user.userId = :userId AND v.voteStatus = :status")
    long countActiveVotesByUser(@Param("userId") Long userId, @Param("status") Vote.VoteStatus status);

    // 오늘 시작하는 일정 조회
    @Query("SELECT v FROM Vote v WHERE DATE(v.startDate) = DATE(:today) AND v.voteStatus = :status")
    List<Vote> findTodayStartingVotes(@Param("today") LocalDateTime today, @Param("status") Vote.VoteStatus status);

    // 오늘 종료되는 일정 조회
    @Query("SELECT v FROM Vote v WHERE DATE(v.dueDate) = DATE(:today) AND v.voteStatus = :status")
    List<Vote> findTodayEndingVotes(@Param("today") LocalDateTime today, @Param("status") Vote.VoteStatus status);

    // 클럽의 최근 투표/일정 조회
    @Query("SELECT v FROM Vote v WHERE v.club.clubId = :clubId ORDER BY v.createdAt DESC")
    Page<Vote> findRecentVotesByClub(@Param("clubId") Long clubId, Pageable pageable);

    // 사용자의 최근 투표/일정 조회
    @Query("SELECT v FROM Vote v WHERE v.user.userId = :userId ORDER BY v.createdAt DESC")
    Page<Vote> findRecentVotesByUser(@Param("userId") Long userId, Pageable pageable);
}