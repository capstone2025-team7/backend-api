package com.capstone2025.team7.backend.userVote.repository;

import com.capstone2025.team7.backend.userVote.entity.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    // ====== 기본 조회 메서드들 ======

    /**
     * 특정 투표의 모든 참여자 조회
     */
    List<UserVote> findByVote_VoteId(Long voteId);

    /**
     * 특정 사용자가 참여한 모든 투표 조회
     */
    List<UserVote> findByUser_UserId(Long userId);

    /**
     * 특정 사용자가 특정 투표에 참여했는지 조회
     */
    Optional<UserVote> findByUser_UserIdAndVote_VoteId(Long userId, Long voteId);

    /**
     * 특정 사용자가 특정 투표에 참여했는지 존재 여부 확인
     */
    boolean existsByUser_UserIdAndVote_VoteId(Long userId, Long voteId);

    /**
     * 특정 투표의 참여자 수 조회
     */
    @Query("SELECT COUNT(uv) FROM user_vote uv WHERE uv.vote.voteId = :voteId")
    Long countByVote_VoteId(@Param("voteId") Long voteId);

    /**
     * 특정 사용자의 투표 참여 수 조회
     */
    @Query("SELECT COUNT(uv) FROM user_vote uv WHERE uv.user.userId = :userId")
    Long countByUser_UserId(@Param("userId") Long userId);

    // ====== 복합 조건 조회 메서드들 ======

    /**
     * 특정 클럽의 모든 투표 참여자들 조회
     */
    @Query("SELECT uv FROM user_vote uv WHERE uv.vote.club.clubId = :clubId")
    List<UserVote> findByVote_Club_ClubId(@Param("clubId") Long clubId);

    /**
     * 활성 상태인 투표에 참여한 사용자들 조회
     */
    @Query("SELECT uv FROM user_vote uv WHERE uv.vote.dueDate > :currentTime")
    List<UserVote> findByActiveVotes(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 특정 사용자가 참여한 활성 투표들 조회
     */
    @Query("SELECT uv FROM user_vote uv WHERE uv.user.userId = :userId AND uv.vote.dueDate > :currentTime")
    List<UserVote> findActiveVotesByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 특정 투표에 참여한 사용자들을 참여일순으로 조회
     */
    @Query("SELECT uv FROM user_vote uv WHERE uv.vote.voteId = :voteId ORDER BY uv.createdAt ASC")
    List<UserVote> findByVote_VoteIdOrderByCreatedAtAsc(@Param("voteId") Long voteId);

    /**
     * 특정 사용자가 참여한 투표들을 최신순으로 조회
     */
    @Query("SELECT uv FROM user_vote uv WHERE uv.user.userId = :userId ORDER BY uv.createdAt DESC")
    List<UserVote> findByUser_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // ====== 삭제 메서드들 ======

    /**
     * 특정 사용자의 특정 투표 참여 삭제
     */
    void deleteByUser_UserIdAndVote_VoteId(Long userId, Long voteId);

    /**
     * 특정 투표의 모든 참여자 삭제 (투표 삭제 시 사용)
     */
    void deleteByVote_VoteId(Long voteId);

    /**
     * 특정 사용자의 모든 투표 참여 삭제 (사용자 삭제 시 사용)
     */
    void deleteByUser_UserId(Long userId);

    // ====== 통계 관련 메서드들 ======

    /**
     * 특정 기간 동안 생성된 투표 참여 건수 조회
     */
    @Query("SELECT COUNT(uv) FROM user_vote uv WHERE uv.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 가장 많이 참여된 투표 TOP N 조회
     */
    @Query("SELECT uv.vote.voteId, COUNT(uv) as participantCount " +
            "FROM user_vote uv " +
            "GROUP BY uv.vote.voteId " +
            "ORDER BY participantCount DESC")
    List<Object[]> findTopVotesByParticipantCount();

    /**
     * 가장 활발한 사용자 TOP N 조회 (투표 참여 많은 순)
     */
    @Query("SELECT uv.user.userId, COUNT(uv) as voteCount " +
            "FROM user_vote uv " +
            "GROUP BY uv.user.userId " +
            "ORDER BY voteCount DESC")
    List<Object[]> findTopUsersByVoteParticipation();
}