package com.capstone2025.team7.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    USER_EXISTS(409, "이미 존재하는 유저입니다."),
    CLUB_NOT_FOUND(404, "동호회를 찾을 수 없습니다."),
    VOTE_NOT_FOUND(404, "투표를 찾을 수 없습니다."),
    PAST_DUE_DATE(409, "이미 지난 날입니다."),
    INVALID_DATE_RANGE(409, "잘못된 값입니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}
