package com.capstone2025.team7.backend.facility.controller;

import com.capstone2025.team7.backend.facility.dto.FacilityInfo;
import com.capstone2025.team7.backend.facility.service.FacilityCrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FacilityCrawlController {
    private final FacilityCrawlService facilityCrawlService;

    @GetMapping("/crawl/sports")
    public void startSportsCrawling() {
        System.out.println("크롤링 요청을 받았습니다...");
        facilityCrawlService.crawlAndSaveAllPages();
    }
}
