package com.example.simplesms.controller;

import com.example.simplesms.dto.like.LikeResponse;
import com.example.simplesms.dto.like.LikeWriteRequest;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.post.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class ApiLikeController {

    private final LikeService likeService;

    @PostMapping
    public APIDataResponse<LikeResponse> like (
            @RequestBody @Valid LikeWriteRequest request,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(likeService.toggleLike(request.postId(), authentication.getId(), request.isLike()));
    }

}
