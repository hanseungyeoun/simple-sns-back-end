package com.example.simplesms.controller;

import com.example.simplesms.controller.ApiLikeController;
import com.example.simplesms.dto.like.LikeResponse;
import com.example.simplesms.dto.like.LikeWriteRequest;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.service.post.LikeService;
import com.example.simplesms.service.user.UserService;
import com.example.simplesms.config.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.UserFixture.userEmailFixture;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Like 컨트롤러")
@WebMvcTest(ApiLikeController.class)
class ApiLikeControllerTest extends MockMvcTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private LikeService likeService;

    @Test
    @DisplayName("[API][POST] 좋아요 요청 - 정상 처리")
    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenLikeInfo_whenRequestingLike_thenUpdatesNewLike() throws Exception {
        //given
        Long postId = 1L;
        Long userId = 1L;
        Boolean isLike = true;

        LikeResponse likeResponse = new LikeResponse(postId, userId, isLike);
        LikeWriteRequest likeWriteRequest = new LikeWriteRequest(postId, true);

        given(likeService.toggleLike(postId, userId, true))
                .willReturn(likeResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/like")
                        .content(objectMapper.writeValueAsBytes(likeWriteRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        then(likeService).should().toggleLike(postId, userId, true);
    }

    @Test
    @DisplayName("[API][POST] 좋아요 요청 - 인증된 사용자가 없을 땐 401 에러")
    void givenUnauthorizedUsr_whenRequestingLike_thenUnauthorizedError() throws Exception {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        LikeWriteRequest likeWriteRequest = new LikeWriteRequest(postId, true);

        // When & Then
        mockMvc.perform(post("/api/v1/like")
                        .content(objectMapper.writeValueAsBytes(likeWriteRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(likeService).should(never()).toggleLike(postId, userId, true);
    }
}