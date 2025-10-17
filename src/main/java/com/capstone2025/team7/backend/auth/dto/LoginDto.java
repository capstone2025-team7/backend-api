package com.capstone2025.team7.backend.auth.dto;

import lombok.Getter;

@Getter
public class LoginDto {
    //이메일
    private String email;
    private String password;
}
