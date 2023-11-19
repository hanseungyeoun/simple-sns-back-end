package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostLike;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.repository.post.PostLikeRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.simplesms.fixture.PostFixture.createPost;
import static com.example.simplesms.fixture.UserFixture.createUser;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@DisplayName("비즈니스 로직 - 좋아요")
@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private  PostLikeRepository likeRepository;


    @DisplayName("존재하지 않는 사용자 정보를 입력하면, 예외를 반환한다.")
    @Test
    void givenNonexistentUserId_whenLikePost_thenThrowsException(){
        //given
        Long postId = 1L;
        Long userId = 1L;

        given(postRepository.findById(userId)).willThrow(EntityNotFoundException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.toggleLike(postId, userId, false))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("존재하지 않는 포스트 정보를 입력하면, 예외를 반환한다.")
    @Test
    void givenNonexistentPostId_whenLikePost_thenThrowsException(){
        //given
        Long postId = 1L;
        Long userId = 1L;

        given(postRepository.findById(any())).willThrow(EntityNotFoundException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.toggleLike(postId, userId, true))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("좋아요를 선택하면 포스트에 좋아요가 반영된다.")
    @Test
    void givenTrue_whenLikePost_thenSavaPostLike(){
        //given
        boolean isLike = true;
        Long userId = 1L;
        Long postId = 1L;


        User user = mock(User.class);
        Post post = mock(Post.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(likeRepository.existsByPostAndUser(post, user)).willReturn(false);

        //when & then
        sut.toggleLike(postId, userId, isLike);
        then(likeRepository).should().save(any(PostLike.class));
    }

    @DisplayName("좋아요를 해지요하면,  포스트의 좋아요가 해지 된다")
    @Test
    void givenFalse_whenLikePost_thenSavaPostLike(){
        //given
        Boolean isLike = false;
        Long userId = 1L;
        Long postId = 1L;

        User user = createUser();
        Post post = createPost();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(likeRepository.existsByPostAndUser(post, user)).willReturn(!isLike);

        //when & then
        sut.toggleLike(postId, userId, isLike);
        then(likeRepository).should().deleteByPostIdAndUserId(postId, userId);
    }
}