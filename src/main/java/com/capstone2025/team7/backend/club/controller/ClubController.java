package com.capstone2025.team7.backend.club.controller;

import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
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
//    @PatchMapping
//    @Operation(summary = "동호회 정보 수정")
//    public ResponseEntity<ClubDto.Response> updateClub(@Valid @RequestBody ClubDto.Patch patchDto) {
//        ClubDto.Response response = clubService.updateClub(patchDto);
//        return ResponseEntity.ok(response);
//    }

    /**
     * 동호회 삭제
     */
    @DeleteMapping("/{clubId}")
    @Operation(summary = "동호회 삭제")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }
}
