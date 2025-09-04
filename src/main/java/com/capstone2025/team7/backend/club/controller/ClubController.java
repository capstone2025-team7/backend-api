package com.capstone2025.team7.backend.club.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 동호회 생성
     */
    @PostMapping
    @Operation(summary = "동호회 생성")
    public ResponseEntity<ClubDto.Response> createClub(@Valid @RequestBody ClubDto.Post postDto) {
        ClubDto.Response response = clubService.createClub(postDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 동호회 조회
     */
    @GetMapping
    @Operation(summary = "전체 동호회 조회")
    public ResponseEntity<List<ClubDto.Response>> getAllClubs() {
        List<ClubDto.Response> response = clubService.getAllClubs();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 동호회 조회
     */
    @GetMapping("/{clubId}")
    @Operation(summary = "단일 동호회 조회")
    public ResponseEntity<ClubDto.Response> getClubById(@PathVariable Long clubId) {
        ClubDto.Response response = clubService.getClubById(clubId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 정보 수정
     */
    @PatchMapping("/{clubId}")
    @Operation(summary = "동호회 정보 수정", description = "동호회의 일부 정보를 수정합니다. 수정하고 싶은 필드만 요청 바디에 포함하면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동호회 수정 성공"),
            @ApiResponse(responseCode = "404", description = "동호회를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<ClubDto.Response> updateClub(
            @Parameter(description = "수정할 동호회 ID", required = true, example = "1")
            @PathVariable Long clubId,
            @Parameter(description = "수정할 동호회 정보 (수정할 필드만 포함)")
            @Valid @RequestBody ClubDto.Patch patchDto) {

        ClubDto.Response response = clubService.updateClub(clubId, patchDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 삭제
     */
    @DeleteMapping("/{clubId}")
    @Operation(summary = "동호회 삭제")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 동호회 가입 신청
     */
    @PostMapping("/{clubId}/users")
    @Operation(summary = "동호회 가입 신청")
    public ResponseEntity<UserClubDto.Response> joinClub(@Valid @RequestBody UserClubDto.Post postDto) {

        UserClubDto.Response response = clubService.joinClub(postDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 동호회 멤버 목록 조회
     */
    @GetMapping("/{clubId}/users")
    @Operation(summary = "동호회 멤버 목록 조회")
    public ResponseEntity<List<UserClubDto.Response>> getClubMembers(@PathVariable Long clubId) {
        List<UserClubDto.Response> members = clubService.getClubUsers(clubId);
        return ResponseEntity.ok(members);
    }

    /**
     * 멤버 제거/탈퇴
     */
    @DeleteMapping("/{clubId}/users/{userClubId}")
    @Operation(summary = "멤버 제거/탈퇴")
    public ResponseEntity<Void> removeUser(
            @PathVariable Long clubId,
            @PathVariable Long userClubId,
            @RequestHeader("User-Id") Long requestUserId) {

        clubService.removeUser(clubId, userClubId, requestUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 활성 동호회 목록 조회
     */
    @GetMapping("/active")
    @Operation(summary = "활성 동호회 목록 조회")
    public ResponseEntity<List<ClubDto.Response>> getActiveClubs() {
        List<ClubDto.Response> activeClubs = clubService.getActiveClubs();
        return ResponseEntity.ok(activeClubs);
    }

    /**
     * 비활성 동호회 목록 조회
     */
    @GetMapping("/inactive")
    @Operation(summary = "비활성 동호회 목록 조회")
    public ResponseEntity<List<ClubDto.Response>> getInactiveClubs() {
        List<ClubDto.Response> inactiveClubs = clubService.getInactiveClubs();
        return ResponseEntity.ok(inactiveClubs);
    }
}