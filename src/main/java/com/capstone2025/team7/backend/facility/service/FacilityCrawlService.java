package com.capstone2025.team7.backend.facility.service;

import com.capstone2025.team7.backend.facility.dto.FacilityInfo;
import com.capstone2025.team7.backend.facility.dto.OperatingHours;
import com.capstone2025.team7.backend.facility.entity.Facility;
import com.capstone2025.team7.backend.facility.mapper.FacilityMapper;
import com.capstone2025.team7.backend.facility.repository.FacilityRepository;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacilityCrawlService {

    private final Browser browser;
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    private String targetUrl = "https://share.gg.go.kr/sports?searchType=S1";
    private String baseUrl = targetUrl.split("\\?")[0];

    public List<FacilityInfo> crawlAllPages() {
        List<FacilityInfo> allResults = new ArrayList<>();
        try (Page page = browser.newPage()) {
            // 1. 초기 사이트 접속 (세션 초기화)
            page.navigate("https://share.gg.go.kr/");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 2. 첫 페이지로 이동하여 마지막 페이지 번호 획득
            page.navigate(baseUrl + "?searchType=S1&curPage=1");
            page.locator("ul.service-card-list > li").first().waitFor(new Locator.WaitForOptions().setTimeout(10000));

            int lastPage = 1; // 기본값
            Locator lastPageButton = page.locator("a.last"); // '마지막 페이지' 버튼 선택
            if (lastPageButton.isVisible()) {
                String href = lastPageButton.getAttribute("href"); // href 속성 가져오기
                Pattern pattern = Pattern.compile("curPage=(\\d+)"); // 정규표현식으로 숫자 추출
                Matcher matcher = pattern.matcher(href);
                if (matcher.find()) {
                    lastPage = Integer.parseInt(matcher.group(1));
                }
            }
            log.info("전체 페이지수: {}", lastPage);

            // 3. 1페이지부터 마지막 페이지까지 순회
            for (int p = 1; p <= lastPage; p++) {
                String currentPageUrl = baseUrl + "?searchType=S1&curPage=" + p;
                log.info("--- {}페이지 크롤링 시작 ---", p);

                page.navigate(currentPageUrl);
                page.locator("ul.service-card-list > li").first().waitFor(new Locator.WaitForOptions().setTimeout(10000));

                List<FacilityInfo> pageResults = crawlFacilityInfo(page);
                allResults.addAll(pageResults);
            }
            return allResults;

        } catch (Exception e) {
            e.printStackTrace();
            return allResults;
        }
    }

    public List<FacilityInfo> crawlFacilityInfo(Page page) {
//        // 매번 새로운 컨텍스트와 페이지에서 작업을 수행하여 안정성을 높입니다.
//        try (Page page = browser.newPage()) {
//            // 1단계: 초기 사이트에 접속하여 쿠키를 받습니다. (매표소 방문)
//            System.out.println("초기 사이트에 접속하여 세션을 초기화합니다...");
//            page.navigate("https://share.gg.go.kr/");
//            page.waitForLoadState(LoadState.NETWORKIDLE); // 페이지가 완전히 로딩되길 기다립니다.
//
//            // 2단계: 이제 쿠키가 있으므로, 진짜 목표 URL로 다시 이동합니다. (놀이기구 탑승)
//            System.out.println("목표 URL로 이동합니다: " + targetUrl);
//            page.navigate(targetUrl);
//            page.waitForLoadState(LoadState.NETWORKIDLE);
//
//            // 👇 [수정된 코드] 데이터가 로딩될 때까지 명시적으로 기다립니다.
//            try {
//                System.out.println("시설 목록이 로딩되기를 기다립니다...");
//                // 'ul.service-card-list > li' 선택자에 해당하는 요소 중 첫 번째 항목이 나타날 때까지 최대 10초간 기다립니다.
//                page.locator("ul.service-card-list > li").first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
//                System.out.println("목록 로딩을 확인했습니다.");
//            } catch (Exception e) {
//                System.err.println("시간 초과: 시설 목록을 찾을 수 없습니다. 웹사이트 구조가 변경되었거나 로딩이 너무 느립니다.");
//                e.printStackTrace();
//                return List.of(); // 목록이 없으면 빈 리스트 반환
//            }
//
//            Locator facilityCards = page.locator("ul.service-card-list > li");
//            int totalCount = facilityCards.count();
//            System.out.println("총 " + totalCount + "개의 체육시설을 찾았습니다.");
//
//            List<FacilityInfo> results = new ArrayList<>();

        List<FacilityInfo> pageResults = new ArrayList<>();
        Locator facilityCards = page.locator("ul.service-card-list > li");
        int totalCount = facilityCards.count();

        for (int i = 0; i < totalCount; i++) {
            // Stale Element 문제를 피하기 위해 매번 locator를 다시 조회합니다.
            Locator currentCard = page.locator("ul.service-card-list > li").nth(i);
            String facilityName = currentCard.locator("div.title.ellipsis02").innerText();
            log.info("--- {}. {}", (i + 1), facilityName + " 크롤링 시작 ---");

            currentCard.click();
            page.waitForSelector("//dt[text()='주소']/following-sibling::dd");

            // 1. '운영시간' 라벨 옆 dd 태그 아래에 있는 모든 li 태그를 찾습니다.
            Locator operatingHoursList = page.locator("//dt[text()='운영시간']/following-sibling::dd//li");

            // 2. 모든 li 태그의 텍스트 내용을 List<String> 형태로 가져옵니다.
            List<String> hours = operatingHoursList.allInnerTexts();

            // 3. 리스트의 각 항목을 줄바꿈(\n)으로 연결하여 하나의 문자열로 만듭니다.
            String operatingHoursText = String.join("\n", hours);

            // 1. '요금' 텍스트를 포함하는 dt 옆 dd 태그 아래에 있는 모든 li 태그를 찾습니다.
            Locator costList = page.locator("//dt[contains(text(),'요금')]/following-sibling::dd//li");
            String costText;

            // 1. '요금' 섹션에 해당하는 li 요소가 존재하는지(개수가 0보다 큰지) 확인합니다.
            if (costList.count() > 0) {
                // 2. 존재하면, 기존 로직대로 텍스트를 가져옵니다.
                List<String> costs = costList.allInnerTexts();
                costText = String.join("\n", costs);
            } else {
                // 3. 존재하지 않으면, '무료'라고 직접 지정합니다.
                costText = "무료";
            }

            // 전화번호 찾는 코드
            Locator contactElements = page.locator("//dl[@class='info-contact']//li");
            List<String> contactList = contactElements.allInnerTexts();

            String phoneNumberFromList = contactList.stream()
                    .filter(text -> text.contains("-"))
                    .findFirst()
                    .orElse("전화번호 없음");

            FacilityInfo info = new FacilityInfo();
            info.setName(facilityName);
            info.setAddress(page.locator("//dt[text()='주소']/following-sibling::dd").innerText());
            info.setImageUrl("https://share.gg.go.kr" + page.locator("#img1").getAttribute("src"));
            info.setReservationUrl(page.url());
            info.setOperatingHours(operatingHoursText);
            info.setReservationPeriod(page.locator("//dt[text()='예약기간']/following-sibling::dd").innerText());
            info.setCost(costText);
            info.setPhone(phoneNumberFromList.trim());
            info.setCategory(page.locator("//dt[text()='구분']/following-sibling::dd").innerText());

            pageResults.add(info);
            log.info("{}를 결과에 추가하였습니다.", info.getName());

            page.goBack();
            page.waitForLoadState(LoadState.NETWORKIDLE);
        }
        return pageResults;
    }

    @Transactional
    public void crawlAndSaveAllPages() {
        List<FacilityInfo> crawledData = crawlAllPages();

        for (FacilityInfo dto : crawledData) {
            facilityRepository.findByName(dto.getName())
                    .ifPresentOrElse(
                            // 데이터가 이미 있으면, 업데이트
                            existingFacility -> {
                                facilityMapper.updateFromDto(dto, existingFacility);
                                log.info("업데이트: {}", dto.getName());
                            },
                            // 데이터가 없으면, 새로 생성
                            () -> {
                                Facility newFacility = facilityMapper.toEntity(dto);
                                facilityRepository.save(newFacility);
                                log.info("새로 추가: {}", dto.getName());
                            }
                    );
        }
    }

    public Map<DayOfWeek, OperatingHours> parseOperatingHours(List<String> hoursList) {
        // EnumMap은 Enum을 키로 사용할 때 최적화된 Map입니다.
        Map<DayOfWeek, OperatingHours> schedule = new EnumMap<>(DayOfWeek.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (String line : hoursList) {
            try {
                // "월요일 : 06:00 ~ 22:00"
                String[] parts = line.split(":"); // ["월요일 ", " 06", "00 ~ 22", "00"] -> 이런 방식은 불안정

                // 더 안정적인 파싱 방법
                String[] dayAndTime = line.split(":", 2); // 첫번째 ":" 기준으로만 나눔 -> ["월요일 ", " 06:00 ~ 22:00"]
                String dayStr = dayAndTime[0].trim();

                String[] timeParts = dayAndTime[1].split("~"); // [" 06:00 ", " 22:00"]
                LocalTime startTime = LocalTime.parse(timeParts[0].trim(), formatter);
                LocalTime endTime = LocalTime.parse(timeParts[1].trim(), formatter);

                // 한글 요일을 DayOfWeek Enum으로 변환
                toDayOfWeek(dayStr).ifPresent(dayOfWeek ->
                        schedule.put(dayOfWeek, new OperatingHours(startTime, endTime))
                );

            } catch (Exception e) {
                log.error("운영시간 파싱 실패: {}", line);
            }
        }
        return schedule;
    }

    /**
     * 한글 요일 문자열을 자바의 DayOfWeek Enum으로 변환합니다.
     */
    private java.util.Optional<DayOfWeek> toDayOfWeek(String dayStr) {
        return switch (dayStr) {
            case "월요일" -> java.util.Optional.of(DayOfWeek.MONDAY);
            case "화요일" -> java.util.Optional.of(DayOfWeek.TUESDAY);
            case "수요일" -> java.util.Optional.of(DayOfWeek.WEDNESDAY);
            case "목요일" -> java.util.Optional.of(DayOfWeek.THURSDAY);
            case "금요일" -> java.util.Optional.of(DayOfWeek.FRIDAY);
            case "토요일" -> java.util.Optional.of(DayOfWeek.SATURDAY);
            case "일요일", "공휴일" -> java.util.Optional.of(DayOfWeek.SUNDAY); // 공휴일을 일요일로 간주
            default -> java.util.Optional.empty();
        };
    }
}
