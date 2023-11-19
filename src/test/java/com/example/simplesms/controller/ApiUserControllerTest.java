package com.example.simplesms.controller;

import com.example.simplesms.controller.ApiUserController;
import com.example.simplesms.dto.user.*;
import com.example.simplesms.fixture.UserFixture;
import com.example.simplesms.config.MockMvcTestSupport;
import com.example.simplesms.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.UserFixture.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User 컨트롤러")
@WebMvcTest(ApiUserController.class)
class ApiUserControllerTest extends MockMvcTestSupport {

    @MockBean
    private UserService userService;

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET] User 상세 정보 조회 - 인증된 사용자, 정상 호출 ")
    @Test
    void givenUserUpdateInfo_whenRequestingUser_thenReturnsUpdateUserInfo() throws Exception {
        // Given
        Long userId = 1L;
        UserProfileUpdateRequest request = createUserProfileUpdateRequest();

        given(userService.updateUserProfile(userId, request)).willReturn(createUserInfoResponse(userEmailFixture));

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/users/me")
                                .file((MockMultipartFile) request.file())
                                .param("nickName", request.nickName())
                                .param("description", request.description())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(print());

        then(userService).should().updateUserProfile(userId, request);
    }

    @DisplayName("[API][GET] 회원 가입 - 정상 호출")
    @Test
    void givenJoinInfo_whenRequestingJoinUser_thenReturnsJoinUserId() throws Exception {
        // Given
        Long userId = 1L;
        UserJoinRequest request = createUserJoinRequest();

        given(userService.join(request)).willReturn(userId);

        // When & Then
        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(userService).should().join(request);
    }

    @DisplayName("[API][GET] 로그인 - 정상 호출")
    @Test
    void givenLoginInfo_whenRequestingJoinUser_thenReturnsLoginUserInfo() throws Exception {
        // Given
        UserLoginRequest request = createUserLoginRequest();
        given(userService.login(userEmailFixture, "pass"))
                .willReturn(UserFixture.createLoginResponse());

        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(userService).should().login(userEmailFixture, "pass");
    }
}