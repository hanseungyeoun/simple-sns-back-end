package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostComment;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.comment.CommentResponse;
import com.example.simplesms.dto.comment.CommentWriteRequest;
import com.example.simplesms.repository.post.PostCommentRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;

    public CommentResponse addComment(CommentWriteRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("회원을 (%s)을 찾을 수 없습니다.", request.userId())));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("포스트 (%s)를 찾을 수 없습니다.", request.postId())));

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .comment(request.comment())
                .build();

        return CommentResponse.fromEntity(commentRepository.save(comment));
    }
}
