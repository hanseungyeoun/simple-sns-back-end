package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.PostComment;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.comment.CommentResponse;
import com.example.simplesms.dto.comment.CommentWriteRequest;
import com.example.simplesms.repository.post.PostCommentRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.simplesms.fixture.PostCommentFixture.createCommentWriteRequest;
import static com.example.simplesms.fixture.PostCommentFixture.createPostComment;
import static com.example.simplesms.fixture.PostFixture.createPost;
import static com.example.simplesms.fixture.UserFixture.createUser;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    PostCommentRepository commentRepository;

    @DisplayName("댓글 저장을 시도 했는데 맞는 User가 없으면, Exception을 발생 시킨다")
    @Test
    void givenUserId_whenSavingComment_thenReturnsException() {
        //givn
        CommentWriteRequest request = createCommentWriteRequest();
        given(userRepository.findById(request.userId())).willReturn(Optional.empty());

        //when & then
        assertThatCode(() -> sut.addComment(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format("회원을 (%s)을 찾을 수 없습니다.", request.userId()));

        then(postRepository).should(never()).findById(anyLong());
        then(commentRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("댓글 저장을 시도했는데 맞는 User가 없으면, Exception을 발생 시킨다")
    void givenPostId_whenSavingComment_thenReturnsException() {
        //givn
        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        CommentWriteRequest request = createCommentWriteRequest();
        assertThatCode(() -> sut.addComment(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format("포스트 (%s)를 찾을 수 없습니다.", request.postId()));

        then(commentRepository).should(never()).save(any());
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    @Test
    void givenCommentInfo_whenSavingPost_thenSavePost() {
        //givn
        long postId = 1L;
        long userId = 1L;
        CommentWriteRequest request = createCommentWriteRequest(postId, userId, "comment");
        PostComment savedComment = createPostComment();

        given(userRepository.findById(userId)).willReturn(Optional.of(createUser()));
        given(postRepository.findById(postId)).willReturn(Optional.of(createPost()));
        given(commentRepository.save(any(PostComment.class))).willReturn(savedComment);

        //when
        CommentResponse result = sut.addComment(request);

        //then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", savedComment.getId())
                .hasFieldOrPropertyWithValue("postId", savedComment.getPost().getId())
                .hasFieldOrPropertyWithValue("userId", savedComment.getUser().getId())
                .hasFieldOrPropertyWithValue("comment", savedComment.getComment());

        then(userRepository).should().findById(userId);
        then(postRepository).should().findById(postId);
        then(commentRepository).should().save(any(PostComment.class));
    }

}