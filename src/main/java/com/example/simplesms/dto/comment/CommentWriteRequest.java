package com.example.simplesms.dto.comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CommentWriteRequest(
        @NotNull(message = "Post ID를 입력해 주세요.")
        Long postId,
        @NotNull(message = "유저 Id를 입력해 주세요!")
        Long userId,
        @NotBlank(message = "댓글을 입력해 주세요!")
        String comment
) {
}
