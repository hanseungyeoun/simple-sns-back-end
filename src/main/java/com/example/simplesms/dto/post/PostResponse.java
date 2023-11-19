package com.example.simplesms.dto.post;

import com.example.simplesms.domain.post.Post;

public record PostResponse(
        Long id,
        String content,
        String postImage
) {
    public static PostResponse fromEntity(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getPostImage()
        );
    }
}
