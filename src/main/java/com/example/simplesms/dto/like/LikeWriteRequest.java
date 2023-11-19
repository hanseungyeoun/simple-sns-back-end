package com.example.simplesms.dto.like;

import javax.validation.constraints.NotNull;

public record LikeWriteRequest(
        @NotNull(message = "Post Id를 입력해 주세요.")
        Long postId,
        @NotNull
        Boolean isLike
) {
}
