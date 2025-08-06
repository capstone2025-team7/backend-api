package com.capstone2025.team7.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    USER_EXISTS(409, "이미 존재하는 유저입니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}
