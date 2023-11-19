package com.example.simplesms.repository.post.querydsl;

import com.example.simplesms.config.IntegrationContainerSupport;
import com.example.simplesms.config.TestJpaConfig;
import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostComment;
import com.example.simplesms.domain.post.PostLike;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.post.PostQueryResponse;
import com.example.simplesms.repository.post.HashtagRepository;
import com.example.simplesms.repository.post.PostCommentRepository;
import com.example.simplesms.repository.post.PostLikeRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.query.PostQueryRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.annotation.InMemoryDataJpaTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post Querydsl Repository 테스트")
//@Import({TestJpaConfig.class})
class PostQuerydslRepositoryTest extends IntegrationContainerSupport {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;


    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = User.BySingUpBuilder()
                .email("email@email.com")
                .nickName("nickName")
                .password("pass")
                .build();

        user2 = User.BySingUpBuilder()
                .email("email@email2.com")
                .nickName("nickName")
                .password("pass")
                .build();

        userRepository.saveAll(List.of(user1, user2));

        Post post1 = new Post(user1, "content1", "imageName1", Set.of(new Hashtag("hashtag")));
        Post post2 = new Post(user2, "content2", "imageName2", Set.of());

        postRepository.saveAll(List.of(post1, post2));
        postLikeRepository.save(new PostLike(post1, user1));
        postLikeRepository.save(new PostLike(post2, user2));
    }

    @AfterEach
    void afterEach() {
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        hashtagRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Post 전체 검색 테스트")
    void findAllTest() {
        //when
        Page<PostQueryResponse> result = postQueryRepository.findPostAll(user1.getId(), Pageable.ofSize(10));

        //then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Post UserId 검색 테스트")
    void findAllByUserIdTest() {
        //given
        User searchUser = user1;

        //when
        Page<PostQueryResponse> result = postQueryRepository.findPostAllByUserId(searchUser.getId(), Pageable.ofSize(10));

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("content")
                .contains("content1");

        assertThat(result.getContent().get(0))
                .hasFieldOrPropertyWithValue("likeCount", 1L)
                .hasFieldOrPropertyWithValue("isLike", true)
                .hasFieldOrPropertyWithValue("nickName", searchUser.getNickName())
                .hasFieldOrPropertyWithValue("profileImage", searchUser.getProfileImage());


    }

    @DisplayName("Post Hashtag 검색 테스트")
    @Test
    void findAllByUserIdAndHashtagName() {
        //given
        String searchText = "hashtag";
        User searchUser = user1;


        //when
        Page<PostQueryResponse> result = postQueryRepository.findPostAllByUserIdAndHashtagName(user1.getId(), searchText, PageRequest.ofSize(10));

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("content")
                .contains("content1");

        assertThat(result.getContent().get(0))
                .hasFieldOrPropertyWithValue("likeCount", 1L)
                .hasFieldOrPropertyWithValue("isLike", true)
                .hasFieldOrPropertyWithValue("nickName", searchUser.getNickName())
                .hasFieldOrPropertyWithValue("profileImage", searchUser.getProfileImage());;
    }
}