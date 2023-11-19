package com.example.simplesms.service.post.query;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.dto.post.*;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.query.PostQueryRepository;
import com.example.simplesms.response.RestPage;
import com.example.simplesms.response.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostQueryService {

    private final PostQueryRepository postQueryRepository;
    private final PostRepository postRepository;


    public Page<PostQueryResponse> getPosts(Long userId, Pageable pageable) {
        var result = postQueryRepository.findPostAll(userId, pageable);
        return RestPage.of(result);
    }

    public Page<PostQueryResponse> getPostsByUserId(Long userId, Pageable pageable) {
        var result = postQueryRepository.findPostAllByUserId(userId, pageable);
        return RestPage.of(result);
    }

    public Page<PostQueryResponse> searchPosts(Long userId, String hashtagName, Pageable pageable) {
        var result = postQueryRepository.findPostAllByUserIdAndHashtagName(userId, hashtagName, pageable);
        return RestPage.of(result);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("포스트(%d)를 찾을 수 없습니다.", postId)));

        return PostResponse.fromEntity(post);
    }

}
