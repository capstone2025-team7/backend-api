package com.capstone2025.team7.backend.vote.controller;

import com.capstone2025.team7.backend.vote.dto.VoteDto;
import com.capstone2025.team7.backend.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vote", description = "투표/일정 관리 API")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @Operation(summary = "투표/일정 생성", description = "새로운 투표 또는 일정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "투표/일정 생성 성공",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "클럽 또는 사용자를 찾을 수 없음")
    })
    public ResponseEntity<VoteDto.Response> createVote(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "투표/일정 생성 정보")
            VoteDto.Post postDto) {
        VoteDto.Response response = voteService.createVote(postDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{vote-id}")
    @Operation(summary = "투표/일정 단건 조회", description = "특정 투표 또는 일정의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "투표/일정을 찾을 수 없음")
    })
    public ResponseEntity<VoteDto.Response> getVote(
            @Parameter(description = "투표/일정 ID", example = "1")
            @PathVariable("vote-id") @Positive Long voteId) {
        VoteDto.Response response = voteService.findVote(voteId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "투표/일정 목록 조회", description = "조건에 따른 투표/일정 목록을 페이징으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ResponseEntity<Page<VoteDto.Response>> getVotes(
            @Parameter(description = "클럽 ID", example = "1")
            @RequestParam(required = false) Long clubId,
            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<VoteDto.Response> responses = voteService.findVotes(clubId, userId, pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PatchMapping("/{vote-id}")
    @Operation(summary = "투표/일정 수정", description = "기존 투표/일정 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "투표/일정을 찾을 수 없음")
    })
    public ResponseEntity<VoteDto.Response> updateVote(
            @Parameter(description = "투표/일정 ID", example = "1")
            @PathVariable("vote-id") @Positive Long voteId,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 투표/일정 정보")
            VoteDto.Patch patchDto) {
        patchDto.setVoteId(voteId);
        VoteDto.Response response = voteService.updateVote(patchDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{vote-id}")
    @Operation(summary = "투표/일정 삭제", description = "투표/일정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "투표/일정을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteVote(
            @Parameter(description = "투표/일정 ID", example = "1")
            @PathVariable("vote-id") @Positive Long voteId) {
        voteService.deleteVote(voteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/club/{club-id}")
    @Operation(summary = "클럽별 투표/일정 조회", description = "특정 클럽의 투표/일정 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "클럽을 찾을 수 없음")
    })
    public ResponseEntity<Page<VoteDto.Response>> getVotesByClub(
            @Parameter(description = "클럽 ID", example = "1")
            @PathVariable("club-id") @Positive Long clubId,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<VoteDto.Response> responses = voteService.findVotesByClub(clubId, pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/user/{user-id}")
    @Operation(summary = "사용자별 투표/일정 조회", description = "특정 사용자가 생성한 투표/일정 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<Page<VoteDto.Response>> getVotesByUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable("user-id") @Positive Long userId,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<VoteDto.Response> responses = voteService.findVotesByUser(userId, pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/active")
    @Operation(summary = "활성 투표/일정 조회", description = "현재 활성 상태인 투표/일정 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<Page<VoteDto.Response>> getActiveVotes(
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<VoteDto.Response> responses = voteService.findActiveVotes(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PatchMapping("/{vote-id}/complete")
    @Operation(summary = "투표/일정 완료", description = "투표/일정의 상태를 완료로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료 처리 성공",
                    content = @Content(schema = @Schema(implementation = VoteDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "투표/일정을 찾을 수 없음")
    })
    public ResponseEntity<VoteDto.Response> completeVote(
            @Parameter(description = "투표/일정 ID", example = "1")
            @PathVariable("vote-id") @Positive Long voteId) {
        VoteDto.Response response = voteService.completeVote(voteId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}