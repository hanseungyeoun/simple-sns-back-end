package com.example.simplesms.controller;

import com.example.simplesms.dto.post.PostResponse;
import com.example.simplesms.dto.post.PostResponseWithHashtag;
import com.example.simplesms.dto.post.PostUpdateRequest;
import com.example.simplesms.dto.post.PostWriteRequest;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiPostController {

    private final PostService postService;



    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIDataResponse<Long> create(
            @Valid PostWriteRequest request,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        PostResponseWithHashtag response = postService.create(authentication.getId(), request);
        return APIDataResponse.success(response.id());
    }

    @PostMapping("/{postId}/renew")
    public APIDataResponse<PostResponseWithHashtag> updatePost(
            @PathVariable Long postId,
            @Valid PostUpdateRequest postUpdateRequest,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(postService.updatePost(postId, authentication.getId(), postUpdateRequest));
    }

    @DeleteMapping("/{postId}")
    public APIDataResponse<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        postService.deletePost(postId, userPrincipal.getId());
        return APIDataResponse.success("OK");
    }
}
