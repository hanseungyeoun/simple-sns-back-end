package com.example.simplesms.dto.post;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.domain.post.Post;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponseWithHashtag(
        Long id,
        String content,
        String postImage,
        Set<String> hashtagName
) {
    public static PostResponseWithHashtag fromEntity(Post post) {
        return new PostResponseWithHashtag(
                post.getId(),
                post.getContent(),
                post.getPostImage(),
                post.getHashtags().stream()
                        .map(Hashtag::getHashtagName)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
