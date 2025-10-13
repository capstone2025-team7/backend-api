package com.capstone2025.team7.backend.facility.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaywrightConfig {
    private Playwright playwright;
    private Browser browser;

    @Bean // 이 메소드가 반환하는 객체를 Spring Bean으로 등록
    public Browser browser() {
        playwright = Playwright.create();
        // 서버 환경에서는 true로 설정해야 GUI 없이 동작합니다.
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        return browser;
    }

    // Spring 애플리케이션이 종료될 때, 생성된 Playwright와 Browser를 안전하게 닫아줍니다.
    @PreDestroy
    void cleanup() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
