package com.example.simplesms.service.post.query;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.dto.post.PostQueryResponse;
import com.example.simplesms.dto.post.PostResponse;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.query.PostQueryRepository;
import com.example.simplesms.response.RestPage;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.service.post.HashtagFactory;
import com.example.simplesms.service.post.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.example.simplesms.fixture.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 포스트 Query")
@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {

    @InjectMocks
    private PostQueryService sut;

    @Mock
    PostQueryRepository postQueryRepository;

    @Mock
    PostRepository postRepository;

    @DisplayName("전체 포스트 페이지를 반환한다.")
    @Test
    void givenNothing_whenSearchingPosts_thenReturnsAllPostPage(){
        //givn
        Long userId = -1L;
        Pageable pageable = Pageable.ofSize(10);
//        given(postCacheRepository.findPostAll(pageable)).willReturn(null);
        given(postQueryRepository.findPostAll(userId, pageable)).willReturn(Page.empty());

        //when
        Page<PostQueryResponse> response = sut.getPosts(userId, pageable);

        //then
        Assertions.assertThat(response).isEqualTo(Page.empty());

        then(postQueryRepository).should().findPostAll(userId, pageable);
    }

    @DisplayName("회원 ID로 검색하면, 내 포스트 페이지를 반환한다.")
    @Test
    void givenUserId_whenSearchingPosts_thenReturnsUserPostPage(){
        //givn
        Long userId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        given(postQueryRepository.findPostAllByUserId(userId, pageable)).willReturn(Page.empty());

        //when
        Page<PostQueryResponse> response = sut.getPostsByUserId(userId, pageable);

        //then
        Assertions.assertThat(response).isEqualTo(Page.empty());
        then(postQueryRepository).should().findPostAllByUserId(userId, pageable);
    }

    @DisplayName("포스트를 해시태그 검색하면, 포스트 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingPostViaHashtag_thenReturnsPostsPage(){
        //givn
        Long userId = 1L;
        String hashtag = "hashtag";
        Pageable pageable = Pageable.ofSize(10);

        given(postQueryRepository.findPostAllByUserIdAndHashtagName(userId, hashtag, pageable))
                .willReturn(Page.empty());

        //when
        Page<PostQueryResponse> response = sut.searchPosts(userId, hashtag, pageable);

        //then
        Assertions.assertThat(response).isEqualTo(Page.empty());
        then(postQueryRepository).should().findPostAllByUserIdAndHashtagName(userId, hashtag, pageable);
    }

    @DisplayName("포스트 Id로 조회하면, 포스트가 존재 하지 않을 시 예외를 발생 시킨다.")
    @Test
    void givenNonexistentPostId_whenSearchingPost_thenReturnsArticleWithComments(){
        //given
        Long postId = 1L;
        given(postRepository.findById(anyLong())).willThrow(EntityNotFoundException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.getPost(postId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("포스트를 조회하면, 포스트를 반환한다.")
    @Test
    void givenPostId_whenSearchingPost_thenReturnsPost() {
        //given
        Long postId = 1L;
        Post post = createPost();
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        //when
        PostResponse response = sut.getPost(postId);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("id", post.getId())
                .hasFieldOrPropertyWithValue("content", post.getContent())
                .hasFieldOrPropertyWithValue("postImage", post.getPostImage());

        then(postRepository).should().findById(postId);
    }

}