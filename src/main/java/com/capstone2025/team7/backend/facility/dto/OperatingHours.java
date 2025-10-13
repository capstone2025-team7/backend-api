package com.capstone2025.team7.backend.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@ToString
public class OperatingHours {
    private LocalTime startTime;
    private LocalTime endTime;
}
