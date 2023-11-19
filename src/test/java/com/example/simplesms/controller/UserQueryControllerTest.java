package com.example.simplesms.controller;

import com.example.simplesms.config.MockMvcTestSupport;
import com.example.simplesms.dto.user.UserDetailInfoResponse;
import com.example.simplesms.service.user.query.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User 컨트롤러 - Query")
@WebMvcTest(ApiUserQueryController.class)
class UserQueryControllerTest extends MockMvcTestSupport {

    @MockBean
    private UserQueryService userQueryService;

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET] User 상세 정보 조회 - 인증된 사용자, 정상 호출 ")
    @Test
    void givenNothing_whenRequestingUser_thenReturnsUserDetailInfo() throws Exception {
        // Given
        Long userId = 1L;

        UserDetailInfoResponse response = createUserDetailInfoResponse();
        given(userQueryService.getUserProfileWithPost(userId)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/users/me/details"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(userQueryService).should().getUserProfileWithPost(userId);
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET] User 조회 - 인증된 사용자, 정상 호출 ")
    @Test
    void givenNothing_whenRequestingUser_thenReturnsUserInfo() throws Exception {
        // Given
        Long userId = 1L;
        given(userQueryService.getUser(userId)).willReturn(createUserInfoResponse(userEmailFixture));

        // When & Then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(userQueryService).should().getUser(userId);
    }
}