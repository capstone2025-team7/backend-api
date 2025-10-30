package com.capstone2025.team7.backend.club.controller;

import com.capstone2025.team7.backend.auth.service.MemberDetailsService.MemberDetail; // MemberDetail import 필요
import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.service.ClubService;
import com.capstone2025.team7.backend.userClub.dto.UserClubDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 동호회 생성 (요구사항: 관리자만 가능)
     */
    @PostMapping
    @Operation(summary = "동호회 생성")
    public ResponseEntity<ClubDto.Response> createClub(
            @Valid @RequestBody ClubDto.Post postDto,
            @AuthenticationPrincipal MemberDetail memberDetail) {

        Long userId = memberDetail.getUserId();
        // Service에서 ROLE_ADMIN 권한 확인 및 Club Owner 설정
        ClubDto.Response response = clubService.createClub(postDto, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 동호회 조회 (요구사항: 일반 회원 가능 - 인증만 필요)
     */
    @GetMapping
    @Operation(summary = "전체 동호회 조회")
    public ResponseEntity<List<ClubDto.Response>> getAllClubs() {
        List<ClubDto.Response> response = clubService.getAllClubs();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 동호회 조회 (요구사항: 일반 회원 가능 - 인증만 필요)
     */
    @GetMapping("/{clubId}")
    @Operation(summary = "단일 동호회 조회")
    public ResponseEntity<ClubDto.Response> getClubById(@PathVariable Long clubId) {
        ClubDto.Response response = clubService.getClubById(clubId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 정보 수정 (요구사항: 관리자만 가능)
     */
    @PatchMapping("/{clubId}")
    @Operation(summary = "동호회 정보 수정", description = "동호회의 일부 정보를 수정합니다. 오너만 수정 가능하도록 Service에서 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동호회 수정 성공"),
            @ApiResponse(responseCode = "404", description = "동호회를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<ClubDto.Response> updateClub(
            @Parameter(description = "수정할 동호회 ID", required = true, example = "1")
            @PathVariable Long clubId,
            @Parameter(description = "수정할 동호회 정보 (수정할 필드만 포함)")
            @Valid @RequestBody ClubDto.Patch patchDto,
            @AuthenticationPrincipal MemberDetail memberDetail) {

        Long userId = memberDetail.getUserId();
        ClubDto.Response response = clubService.updateClub(clubId, patchDto, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 삭제 (요구사항: 관리자만 가능)
     */
    @DeleteMapping("/{clubId}")
    @Operation(summary = "동호회 삭제")
    public ResponseEntity<Void> deleteClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal MemberDetail memberDetail) {

        Long userId = memberDetail.getUserId();
        clubService.deleteClub(clubId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 동호회 가입 신청 (요구사항: 일반 회원 가능)
     */
    @PostMapping("/{clubId}/users")
    @Operation(summary = "동호회 가입 신청")
    public ResponseEntity<UserClubDto.Response> joinClub(
            @Valid @RequestBody UserClubDto.Post postDto,
            @AuthenticationPrincipal MemberDetail memberDetail) {

        Long userId = memberDetail.getUserId();
        // Service에서 postDto의 userId 대신 토큰의 userId를 사용합니다.
        UserClubDto.Response response = clubService.joinClub(postDto, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 멤버 목록 조회 (요구사항: 해당 동호회 회원만 가능)
     */
    @GetMapping("/{clubId}/users")
    @Operation(summary = "동호회 멤버 목록 조회")
    public ResponseEntity<List<UserClubDto.Response>> getClubMembers(
            @PathVariable Long clubId,
            @AuthenticationPrincipal MemberDetail memberDetail) { // 🚩 인가 확인을 위해 userId 필요

        Long userId = memberDetail.getUserId();
        // Service에서 요청자가 해당 클럽의 멤버인지 확인합니다.
        List<UserClubDto.Response> members = clubService.getClubUsers(clubId, userId);
        return ResponseEntity.ok(members);
    }

    /**
     * 멤버 제거/탈퇴 (요구사항: 일반 회원 가능 - 본인 탈퇴/취소)
     */
    @DeleteMapping("/{clubId}/users/{userClubId}")
    @Operation(summary = "동호회 멤버 제거 또는 가입 신청 취소/탈퇴",
            description = "userClubId를 통해 특정 동호회 가입 기록을 삭제합니다. 요청자 ID는 JWT 토큰에서 안전하게 가져옵니다.")
    public ResponseEntity<Void> removeUser(
            @PathVariable Long clubId,
            @PathVariable Long userClubId,
            @AuthenticationPrincipal MemberDetail memberDetail) {

        Long requestUserId = memberDetail.getUserId();
        clubService.withdrawClub(clubId, userClubId, requestUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 활성 동호회 목록 조회 (요구사항: 일반 회원 가능 - 인증만 필요)
     */
    @GetMapping("/active")
    @Operation(summary = "활성 동호회 목록 조회")
    public ResponseEntity<List<ClubDto.Response>> getActiveClubs() {
        List<ClubDto.Response> activeClubs = clubService.getActiveClubs();
        return ResponseEntity.ok(activeClubs);
    }

    /**
     * 비활성 동호회 목록 조회 (요구사항: 일반 회원 가능 - 인증만 필요)
     */
    @GetMapping("/inactive")
    @Operation(summary = "비활성 동호회 목록 조회")
    public ResponseEntity<List<ClubDto.Response>> getInactiveClubs() {
        List<ClubDto.Response> inactiveClubs = clubService.getInactiveClubs();
        return ResponseEntity.ok(inactiveClubs);
    }
}