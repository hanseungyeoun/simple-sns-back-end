package com.example.simplesms.dto.comment;

import com.example.simplesms.domain.post.PostComment;

public record CommentResponse(
        Long id,
        Long postId,
        Long userId,
        String comment
) {

    public static CommentResponse fromEntity(PostComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getComment()
        );
    }
}
