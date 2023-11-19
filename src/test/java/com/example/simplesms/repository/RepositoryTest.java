package com.example.simplesms.repository;

import com.example.simplesms.annotation.InMemoryDataJpaTest;
import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostComment;
import com.example.simplesms.domain.post.PostLike;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.repository.post.PostCommentRepository;
import com.example.simplesms.repository.post.PostLikeRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Post Repository 테스트")
@InMemoryDataJpaTest
public class RepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCommentRepository commentRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    EntityManager em;
    private User user;
    private Hashtag hashtag1;
    private Hashtag hashtag2;

    @BeforeEach
    void beforeEach() {
        user = User.BySingUpBuilder()
                .email("email")
                .password("password")
                .nickName("nickName")
                .build();

        userRepository.save(user);

        hashtag1 = new Hashtag("hashtag1");
        hashtag2 = new Hashtag("hashtag2");
    }

    @Test
    @DisplayName("Post + hashtag 저장 테스트")
    public void post_save_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        //when
        postRepository.save(post);

        //then
        em.flush();
        em.clear();

        Post findPost = postRepository.findById(post.getId()).get();

        assertThat(findPost).hasFieldOrPropertyWithValue("content", "content")
                .hasFieldOrPropertyWithValue("postImage", "postImage")
                .extracting("hashtags", as(COLLECTION))
                .extracting("hashtagName")
                .contains("hashtag1", "hashtag2");
    }

    @Test
    @DisplayName("Post + Comment 저장 테스트")
    public void post_comment_save_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        postRepository.save(post);

        PostComment postComment1 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment1")
                .build();

        PostComment postComment2 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment2")
                .build();

        commentRepository.save(postComment1);
        commentRepository.save(postComment1);

        //when && then
        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost).hasFieldOrPropertyWithValue("content", "content")
                .hasFieldOrPropertyWithValue("postImage", "postImage")
                .extracting("hashtags", as(COLLECTION))
                .extracting("hashtagName")
                .contains("hashtag1", "hashtag2");

        assertThat(findPost.getComments())
                .hasSize(2)
                .extracting("comment")
                .contains("comment1", "comment2");
    }


    @Test
    @DisplayName("Post + Comment 삭제 테스트")
    public void post_delete_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        postRepository.save(post);

        PostComment postComment1 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment1")
                .build();

        PostComment postComment2 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment2")
                .build();

        commentRepository.save(postComment1);
        commentRepository.save(postComment1);

        //when
        Post findPost = postRepository.findById(post.getId()).get();
        postRepository.deleteById(post.getId());
        postRepository.flush();


        //then
        Assertions.assertThatCode(
                () ->postRepository.findById(post.getId()).get()
        )  .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Post + Comment 삭제 테스트")
    public void post_comment_delete_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        postRepository.save(post);

        PostComment postComment1 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment1")
                .build();

        PostComment postComment2 = PostComment.builder()
                .post(post)
                .user(user)
                .comment("comment2")
                .build();

        commentRepository.save(postComment1);
        commentRepository.save(postComment2);
        em.flush();
        em.clear();

        //when
        commentRepository.deleteById(postComment1.getId());
        em.flush();
        em.clear();

        //then
        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost).hasFieldOrPropertyWithValue("content", "content")
                .hasFieldOrPropertyWithValue("postImage", "postImage")
                .extracting("hashtags", as(COLLECTION))
                .extracting("hashtagName")
                .contains("hashtag1", "hashtag2");

        assertThat(findPost.getComments())
                .hasSize(1)
                .extracting("comment")
                .containsExactly("comment2");
    }

    @DisplayName("좋아요 저장 테스트")
    @Test
    public void post_like_save_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        postRepository.save(post);

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        //when
        postLikeRepository.save(like);

        //then
        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost.getLikes().size()).isEqualTo(1);
    }

    @DisplayName("좋아요 삭제 테스트")
    @Test
    public void post_like_delete_test() {
        //givn
        Post post = Post.builder()
                .user(user)
                .content("content")
                .hashtags(Set.of(hashtag1, hashtag2))
                .postImage("postImage")
                .build();

        postRepository.save(post);

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        PostLike postLike = postLikeRepository.save(like);
        em.flush();
        em.clear();

        //when
        Post post1 = postRepository.findById(post.getId()).get();
        Set<PostLike> likes = post1.getLikes();
        post1.removeLike(postLike);
        postLikeRepository.deleteById(like.getId());

        //then
        em.flush();
        em.clear();

        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost.getLikes().size()).isZero();
    }

}

