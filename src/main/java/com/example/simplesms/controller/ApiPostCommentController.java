package com.example.simplesms.controller;

import com.example.simplesms.dto.comment.CommentResponse;
import com.example.simplesms.dto.comment.CommentWriteRequest;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.post.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class ApiPostCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public APIDataResponse<CommentResponse> addComment(
            @RequestBody @Valid CommentWriteRequest request,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(commentService.addComment(request));
    }
}
