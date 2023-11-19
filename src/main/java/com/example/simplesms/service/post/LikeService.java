package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.PostLike;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.like.LikeResponse;
import com.example.simplesms.repository.post.PostLikeRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.response.exception.IllegalStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;

    public LikeResponse toggleLike(Long postId, Long userId, Boolean isLike) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("PostService.create post is not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("PostService.create post is not found"));

        if(isLike == true){
            onLike(post, user);
        } else {
            offLike(post, user);
        }
        return new LikeResponse(postId, userId, isLike);
    }

    private void onLike(Post post, User user) {
        boolean isExists =  likeRepository.existsByPostAndUser(post, user);
        if(!isExists) {
            likeRepository.save(new PostLike(post, user));
        } else {
           throw  new IllegalStatusException("잘 못된 값입니다.");

        }
    }

    private void offLike(Post post, User user) {
        boolean isExists =  likeRepository.existsByPostAndUser(post, user);
        if(isExists) {
            likeRepository.deleteByPostIdAndUserId(post.getId(), user.getId());
        } else {
            throw new IllegalStatusException("잘 못된 값입니다.");
        }
    }

    public Integer getLikeCountByUser(Long userId) {
        return likeRepository.countByUser_Id(userId);
    }

}
