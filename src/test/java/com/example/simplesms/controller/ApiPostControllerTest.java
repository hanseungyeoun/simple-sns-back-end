package com.example.simplesms.controller;

import com.example.simplesms.dto.post.PostUpdateRequest;
import com.example.simplesms.dto.post.PostWriteRequest;
import com.example.simplesms.service.post.query.PostQueryService;
import com.example.simplesms.response.RestPage;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.config.MockMvcTestSupport;
import com.example.simplesms.service.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.PostFixture.*;
import static com.example.simplesms.fixture.UserFixture.userEmailFixture;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Post 컨트롤러")
@WebMvcTest(ApiPostController.class)
class ApiPostControllerTest extends MockMvcTestSupport {

    @MockBean
    private PostService postService;

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][POST] 새 포스트 등록 - 정상 호출")
    @Test
    void givenPostInfo_whenRequestingSavePost_thenSavesNewPost() throws Exception {
        //Given
        Long userId = 1L;
        PostWriteRequest postWriteRequest = createWriteRequest();

        given(postService.create(eq(userId), any()))
                .willReturn(createPostResponseWithHashtag());

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts")
                                .file((MockMultipartFile) postWriteRequest.file())
                                .param("content", postWriteRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(print());


        then(postService).should().create(eq(userId), any());
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][POST] 새 포스트 등록 - Invalid value 입력 시  Bed Request Error 에러")
    @Test
    void givenNotValidPostInfo_whenRequestingSavePost_thenBedRequestError() throws Exception {
        // Given
        Long userId = 1L;
        PostWriteRequest postWriteRequest = createWriteRequest(null);

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts")
                                .file((MockMultipartFile) postWriteRequest.file())
                                .param("content", postWriteRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is(ErrorCode.COMMON_INVALID_PARAMETER.getStatus().value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.errors[0].field").value("content"))
                .andExpect(jsonPath("$.errors[0].reason").value("사진 설명을 입력해 주세요!"))
                .andDo(print());

        then(postService).should(never()).create(eq(userId), any());
    }

    @Test
    @DisplayName("[API][POST] 새 포스트 등록 - 인증된 사용자가 없을 땐 401 에러")
    void givenUnauthorizedUsr_whenRequestingSavePost_thenUnauthorizedError() throws Exception {
        // Given
        Long userId = 1L;
        PostWriteRequest postWriteRequest = createWriteRequest(null);

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts")
                                .file((MockMultipartFile) postWriteRequest.file())
                                .param("content", postWriteRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(postService).should(never()).create(eq(userId), any());
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Post 수정 수정 - 정상 호출")
    @Test
    void givenUpdatedPostInfo_whenRequestingUpdatePost_thenUpdatesNewPost() throws Exception {
        // Given
        long postId = 1L;
        long userId = 1L;

        PostUpdateRequest postUpdateRequest = createPostUpdateRequest();
        given(postService.updatePost(postId, userId, postUpdateRequest)).willReturn(createPostResponseWithHashtag());;

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts/" + postId + "/renew")
                                .file((MockMultipartFile) postUpdateRequest.file())
                                .param("content", postUpdateRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(print());

        then(postService).should().updatePost(postId, userId, postUpdateRequest);
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][POST] Post 수정 수정 - Invalid value 입력 시  Bed Request Error 에러")
    @Test
    void givenNotValidUpdatedPostInfo_whenRequestingUpdatePost_thenBedRequestError() throws Exception {
        // Given
        long postId = 1L;
        long userId = 1L;

        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("");
        //given(postService.updatePost(id, userId, postUpdateRequest)).willReturn(createPostResponse());;

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts/" + postId + "/renew")
                                .file((MockMultipartFile) postUpdateRequest.file())
                                .param("commentDto", postUpdateRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is(ErrorCode.COMMON_INVALID_PARAMETER.getStatus().value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(postService).should(never()).updatePost(postId, userId, postUpdateRequest);
    }

    @Test
    @DisplayName("[API][POST] 포스트 수정 - 인증된 사용자가 없을 땐 401 에러")
    void givenUnauthorizedUsr_whenRequestingUpdatePost_thenUnauthorizedError() throws Exception {
        // Given
        Long userId = 1L;
        PostWriteRequest postWriteRequest = createWriteRequest(null);

        // When & Then
        mockMvc.perform(
                        multipart("/api/v1/posts")
                                .file((MockMultipartFile) postWriteRequest.file())
                                .param("content", postWriteRequest.content())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(postService).should(never()).create(eq(userId), any());
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET] Post 삭제 - 정상 호출")
    @Test
    void givenDeletePostInfo_whenRequestingDeletePost_thenReturnsOk() throws Exception {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        willDoNothing().given(postService).deletePost(eq(postId), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        then(postService).should().deletePost(eq(postId), anyLong());
    }

    @Test
    @DisplayName("[API][DELETE] 포스트 삭제 - 인증된 사용자가 없을 땐 401 에러")
    void givenUnauthorizedUsr_whenRequestingDeletePost_thenUnauthorizedError() throws Exception {
        // Given
        Long postId = 1L;
        Long userId = 1L;

        willDoNothing().given(postService).deletePost(eq(postId), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());


        then(postService).should(never()).deletePost(eq(postId), anyLong());
    }
}