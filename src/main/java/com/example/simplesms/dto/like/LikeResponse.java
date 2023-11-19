package com.example.simplesms.dto.like;

public record LikeResponse (
        Long postId,
        Long userId,
        Boolean isLike
) {
}
