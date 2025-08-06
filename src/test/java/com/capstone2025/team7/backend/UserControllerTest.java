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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        // 먼저 사용자를 생성
        ResultActions createActions = mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
        );
        String responseString = createActions.andReturn().getResponse().getHeader("Location");
        long userId = Long.parseLong(responseString.substring(responseString.lastIndexOf('/') + 1));

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
        String responseString = createActions.andReturn().getResponse().getHeader("Location");
        long userId = Long.parseLong(responseString.substring(responseString.lastIndexOf('/') + 1));

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
        String responseString = createActions.andReturn().getResponse().getHeader("Location");
        long userId = Long.parseLong(responseString.substring(responseString.lastIndexOf('/') + 1));

        // when
        ResultActions deleteActions = mockMvc.perform(
                delete("/api/users/{userId}", userId)
        );

        // then
        deleteActions.andExpect(status().isNoContent());
    }
}