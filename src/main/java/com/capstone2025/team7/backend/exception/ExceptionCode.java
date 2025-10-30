package com.capstone2025.team7.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    USER_CLUB_NOT_FOUND(404, "유저클럽을 찾을 수 없습니다."),
    USER_EXISTS(409, "이미 존재하는 유저입니다."),
    UNAUTHORIZED_USER(409, "인증되지 않은 유저입니다."),
    CLUB_NOT_FOUND(404, "동호회를 찾을 수 없습니다."),
    CLUB_POPULATION_FULL(409, "동호회가 꽉 찼습니다."),
    VOTE_NOT_FOUND(404, "투표를 찾을 수 없습니다."),
    PAST_DUE_DATE(409, "이미 지난 날입니다."),
    INVALID_DATE_RANGE(409, "잘못된 값입니다."),
    NO_PERMISSION(409, "접근 권한이 없습니다."),
    CLUB_MISMATCH(409, "동호회를 잘못 매칭하셨습니다"),
    NO_PERMISSION_ACCESS_CLUB_MEMBERS(409, "동호회 회원에 접근할 수 없습니다."),
    NICKNAME_EXISTS(409, "이미 존재하는 닉네임 입니다");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}
