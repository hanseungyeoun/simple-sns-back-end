package com.example.simplesms.controller;

import com.example.simplesms.config.MockMvcTestSupport;
import com.example.simplesms.response.RestPage;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.service.post.query.PostQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.example.simplesms.fixture.PostFixture.createPostResponse;
import static com.example.simplesms.fixture.UserFixture.userEmailFixture;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("Post 컨트롤러 - Query")
@WebMvcTest(ApiPostQueryController.class)
class ApiPostQueryControllerTest extends MockMvcTestSupport {

    @MockBean
    private PostQueryService postQueryService;

    @DisplayName("[API][GET] Post 조회 Api - 인증된 유저 없음, 정상 호출")
    @Test
    void givenNothing_whenRequestingPostList_thenReturnsPostList() throws Exception {
        // Given
        Long userId = -1L;
        given(postQueryService.getPosts(eq(userId), any(PageRequest.class))).willReturn(RestPage.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(postQueryService).should().getPosts(eq(userId), any(PageRequest.class));
    }

    @DisplayName("[API][GET] Post 조회 Api - 인증된 유저, 정상 호출")
    @Test
    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenAuthorizedUser_whenRequestingPostList_thenReturnsPostList() throws Exception {
        // Given
        Long userId = 1L;
        given(postQueryService.getPosts(eq(userId), any(PageRequest.class))).willReturn(RestPage.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(postQueryService).should().getPosts(eq(userId), any(PageRequest.class));
    }

    @DisplayName("[API][GET]  My Post 조회 Api - 인증된 사용자가 없을 땐 401 에러")
    @Test
    void givenNoting_whenRequestingPostList_thenReturnsMyPostList() throws Exception {
        // Given
        Long userId = 1L;
        given(postQueryService.getPostsByUserId(any(), any(Pageable.class))).willReturn(RestPage.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/posts/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(postQueryService).should(never()).getPostsByUserId(eq(userId), any(Pageable.class));
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET]  My Post 조회 Api - 인증된 사용자, 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingMyPostList_thenReturnsMyPostList() throws Exception {
        // Given
        Long userId = 1L;
        given(postQueryService.getPostsByUserId(eq(userId), any(Pageable.class))).willReturn(RestPage.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/posts/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        then(postQueryService).should().getPostsByUserId(eq(userId), any(Pageable.class));
    }

    @DisplayName("[API][GET]  Post 검색 - 검색어와 함게, 인증된 사용자가 없을 땐 401 에러")
    @Test
    void givenUnAuthorizedUser_whenSearchingPost_thenReturnsPostList() throws Exception {
        // Given
        String hashtag = "tag";

        // When & Then
        mockMvc.perform(
                        get("/api/v1/posts/search/" + hashtag)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(ErrorCode.COMMON_UNAUTHORIZED.getErrorMsg()))
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andDo(print());

        then(postQueryService).should(never()).searchPosts(any(), any(), any(Pageable.class));
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET]  Post 검색 - 검색어와 함게, 인증된 유저, 정상 호출")
    @Test
    void givenSearchKeyword_whenSearchingPost_thenReturnsPostList() throws Exception {
        // Given
        Long userId = 1L;
        String hashtag = "tag";

        given(postQueryService.searchPosts(eq(userId), eq(hashtag), any(Pageable.class)))
                .willReturn(Page.empty());

        // When & Then
        mockMvc.perform(
                        get("/api/v1/posts/search/" + hashtag)
                )
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(postQueryService).should().searchPosts(eq(userId), eq(hashtag), any(Pageable.class));
    }

    @WithUserDetails(value = userEmailFixture, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[API][GET] Post 조회 - 정상 호출, 인증된 사용자")
    @Test
    void givenPostId_whenRequestingPost_thenReturnsPostInfo() throws Exception {
        // Given
        Long postId = 1L;
        given(postQueryService.getPost(postId)).willReturn(createPostResponse());

        // When & Then
        mockMvc.perform(get("/api/v1/posts/" + postId))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        then(postQueryService).should().getPost(postId);
    }

}