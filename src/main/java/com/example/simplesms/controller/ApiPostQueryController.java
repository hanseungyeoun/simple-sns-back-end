package com.example.simplesms.controller;

import com.example.simplesms.dto.post.PostQueryResponse;
import com.example.simplesms.dto.post.PostResponse;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.post.query.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiPostQueryController {
    private static final int UNKNOWN_USER_ID = -1;
    private final PostQueryService postQueryService;

    @GetMapping
    public APIDataResponse<Page<PostQueryResponse>> list(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        Page<PostQueryResponse> posts = postQueryService.getPosts(authentication != null ? authentication.getId() : UNKNOWN_USER_ID, pageable);
        return APIDataResponse.success(posts);
    }

    @GetMapping("/me")
    public APIDataResponse<Page<PostQueryResponse>> myPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(postQueryService.getPostsByUserId(authentication.getId(), pageable));
    }

    @GetMapping("/search/{hashtag}")
    public APIDataResponse<Page<PostQueryResponse>> search(
            @PathVariable String hashtag,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(postQueryService.searchPosts(authentication != null ? authentication.getId() : UNKNOWN_USER_ID, hashtag, pageable));
    }

    @GetMapping("/{postId}")
    public APIDataResponse<PostResponse> getPost(
            @PathVariable Long postId
    ) {
        return APIDataResponse.success(postQueryService.getPost(postId));
    }

}
