package com.capstone2025.team7.backend;

import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto.Post postDto;

    @BeforeEach
    void setUp() {
        postDto = new UserDto.Post();
        postDto.setName("testuser");
        postDto.setNickname("testnick");
        postDto.setPassword("password123");
        postDto.setProfileImage("profile.jpg");
        postDto.setAge(25);
        postDto.setGender(User.gender.MALE);
        postDto.setEmail("test@example.com");
        postDto.setAvailableDays(List.of("MONDAY"));
    }

    @Test
    void createUserTest() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );

        // then
        actions.andExpect(status().isCreated());
    }

    @Test
    void updateUserTest() throws Exception {
        // given
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        long userId = getUserIdFromLocation(createActions);

        UserDto.Patch patchDto = new UserDto.Patch();
        patchDto.setNickname("newnick");

        // when
        ResultActions patchActions = mockMvc.perform(
                patch("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto))
        );

        // then
        patchActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("newnick"));
    }

    @Test
    void getUserTest() throws Exception {
        // given
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        long userId = getUserIdFromLocation(createActions);

        // when
        ResultActions getActions = mockMvc.perform(
                get("/api/users/{userId}", userId)
        );

        // then
        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testuser"));
    }

    @Test
    void deleteUserTest() throws Exception {
        // given
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        long userId = getUserIdFromLocation(createActions);

        // when
        ResultActions deleteActions = mockMvc.perform(
                delete("/api/users/{userId}", userId)
        );

        // then
        deleteActions.andExpect(status().isNoContent());
    }

    @Test
    void testUpdateAndGetUserAvailableDays() throws Exception {
        // given
        postDto.setAvailableDays(List.of("MONDAY", "WEDNESDAY"));
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        long userId = getUserIdFromLocation(createActions);

        // when & then: Get initial available days
        mockMvc.perform(get("/api/users/{userId}/available-days", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableDays", containsInAnyOrder("MONDAY", "WEDNESDAY")));

        // given: Update available days
        UserDto.AvailableDaysUpdateRequest updateRequest = new UserDto.AvailableDaysUpdateRequest();
        updateRequest.setAvailableDays(List.of("TUESDAY", "FRIDAY"));

        // when & then: Update and check response
        mockMvc.perform(put("/api/users/{userId}/available-days", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("가능 요일이 성공적으로 업데이트되었습니다."));

        // when & then: Get updated available days
        mockMvc.perform(get("/api/users/{userId}/available-days", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableDays", containsInAnyOrder("TUESDAY", "FRIDAY")));
    }

    @Test
    void testGetUsersAvailableOnDays() throws Exception {
        // given: Create users with different available days
        createUser("userA", "userA@example.com", List.of("MONDAY", "TUESDAY"));
        createUser("userB", "userB@example.com", List.of("MONDAY", "WEDNESDAY"));
        createUser("userC", "userC@example.com", List.of("TUESDAY", "THURSDAY"));

        // when & then: Find users available on MONDAY
        mockMvc.perform(get("/api/users/available-on-days").param("days", "MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.nickname == 'userA' || @.nickname == 'userB')]").exists());

        // when & then: Find users available on MONDAY and TUESDAY
        mockMvc.perform(get("/api/users/available-on-days").param("days", "MONDAY,TUESDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nickname").value("userA"));

        // when & then: Find users available on FRIDAY
        mockMvc.perform(get("/api/users/available-on-days").param("days", "FRIDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCheckUserAvailableOnDay() throws Exception {
        // given
        postDto.setAvailableDays(List.of("MONDAY", "FRIDAY"));
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        long userId = getUserIdFromLocation(createActions);

        // when & then: Check for an available day
        mockMvc.perform(get("/api/users/{userId}/available-on/{day}", userId, "MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(true));

        // when & then: Check for an unavailable day
        mockMvc.perform(get("/api/users/{userId}/available-on/{day}", userId, "TUESDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }

    // Helper method to create a user
    private void createUser(String nickname, String email, List<String> availableDays) throws Exception {
        UserDto.Post userDto = new UserDto.Post();
        userDto.setName("testuser");
        userDto.setNickname(nickname);
        userDto.setPassword("password123");
        userDto.setProfileImage("profile.jpg");
        userDto.setAge(25);
        userDto.setGender(User.gender.MALE);
        userDto.setEmail(email);
        userDto.setAvailableDays(availableDays);

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(status().isCreated());
    }

    // Helper method to extract userId from Location header
    private long getUserIdFromLocation(ResultActions actions) throws Exception {
        String location = actions.andReturn().getResponse().getHeader("Location");
        if (location == null) {
            throw new IllegalStateException("Location header not found");
        }
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
