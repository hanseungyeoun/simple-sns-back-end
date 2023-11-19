package com.example.simplesms.controller;

import com.example.simplesms.controller.ApiPostCommentController;
import com.example.simplesms.dto.comment.CommentResponse;
import com.example.simplesms.dto.comment.CommentWriteRequest;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.service.post.CommentService;
import com.example.simplesms.config.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.PostCommentFixture.createCommentWriteRequest;
import static com.example.simplesms.fixture.UserFixture.userEmailFixture;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Comment 컨트롤러")
@WebMvcTest(ApiPostCommentController.class)
class ApiPostCommentControllerTest extends MockMvcTestSupport {

    @MockBean private CommentService commentService;

    @Test
    @DisplayName("[API][POST] 댓글 저장 요청 - 정상 처리")
    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenCommentInfo_whenRequestingSavesComment_thenSavesNewComment() throws Exception {
        //given
        Long id = 1L;
        Long postId = 1L;
        Long userId = 1L;
        String content = "content";

        CommentWriteRequest commentWriteRequest = createCommentWriteRequest();
        given(commentService.addComment(commentWriteRequest)).willReturn(new CommentResponse(id, postId,userId, content));

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentWriteRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        then(commentService).should().addComment(commentWriteRequest);
    }

    @Test
    @DisplayName("[API][POST] 댓글 저장 요청 - 인증된 사용자가 없을 땐 401 에러")
    void givenUnauthorizedUsr_whenRequestingLike_thenUnauthorizedError() throws Exception {
        // Given
        Long id = 1L;
        Long postId = 1L;
        Long userId = 1L;
        String content = "content";

        CommentWriteRequest commentWriteRequest = createCommentWriteRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentWriteRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(commentService).should(never()).addComment(commentWriteRequest);
    }

    @Test
    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][POST] 댓글 저장 요청 - Invalid value 입력 시  Bed Request Error 에러")
    void givenInvalidInfo_whenRequestingLike_thenBedRequest() throws Exception {
        // Given
        Long postId = null;
        Long userId = 1L;
        String content = "";

        CommentWriteRequest commentWriteRequest = createCommentWriteRequest(postId, userId, "ee");

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentWriteRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(ErrorCode.COMMON_INVALID_PARAMETER.getStatus().value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(commentService).should(never()).addComment(commentWriteRequest);
    }
}