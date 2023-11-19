package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.post.*;
import com.example.simplesms.repository.post.HashtagRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.RestPage;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.response.exception.IllegalStatusException;
import com.example.simplesms.service.upload.FileUploder;

import static com.example.simplesms.fixture.HashtagFixture.createHashtag;
import static com.example.simplesms.fixture.PostFixture.*;
import static com.example.simplesms.fixture.PostFixture.createPostUpdateRequest;
import static com.example.simplesms.fixture.UserFixture.createUser;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@DisplayName("비즈니스 로직 - 포스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService sut;

    @Mock
    private PostRepository postRepository;
    @Mock
    private FileUploder fileUploder;

    @Mock
    UserRepository userRepository;

    @Mock
    HashtagFactory hashtagFactory;

    @Mock
    HashtagRepository hashtagRepository;


    @DisplayName("존재하지 않는 사용자 정보를 입력하면, 예외를 반환한다.")
    @Test
    void givenNonexistentUserId_whenSavingPost_thenThrowsException() {
        //given
        Long userId = 1L;
        PostWriteRequest request = createWriteRequest();

        given(userRepository.findById(anyLong())).willThrow(EntityNotFoundException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.create(userId, request))
                .isInstanceOf(EntityNotFoundException.class);

        then(fileUploder).shouldHaveNoInteractions();
        then(postRepository).shouldHaveNoInteractions();
    }

    @DisplayName("포스트 생성 시 이미지 업로드 실패 시, 예외를 반환한다.")
    @Test
    void givenNotValidImageFile_whenSavingPost_thenIllegalStatusException() throws IOException {
        //given
        Long userId = 1L;
        PostWriteRequest request = createWriteRequest();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(fileUploder.storeFile(request.file())).willThrow(IOException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.create(userId, request))
                .isInstanceOf(IllegalStatusException.class)
                .hasMessage("이미지 파일 저장에 실패 하였습니다.");

        then(userRepository).should().findById(anyLong());
        then(postRepository).shouldHaveNoInteractions();
    }

    @DisplayName("없는 포스트의 수정 정보를 입력하면, 예외를 반환한다.")
    @Test
    void givenNonexistentPost_whenUpdatingPost_thenThrowsException(){
        //given
        Long postId = 1L;
        Long userId = 1L;
        PostUpdateRequest request = createPostUpdateRequest();

        given(postRepository.findById(postId)).willThrow(EntityNotFoundException.class);

        //when & then
        Assertions.assertThatCode(() -> sut.updatePost(postId, userId, request))
                .isInstanceOf(EntityNotFoundException.class);

        // Then
        then(fileUploder).shouldHaveNoInteractions();
    }

    @DisplayName("포스트 작성자가 아닌 사람이 수정 정보를 입력하면, 예외를 반환한다.")
    @Test
    void givenModifiedPostInfoWithDifferentUser_whenUpdatingPost_thenThrowsIllegalStatusException(){
        //given
        Long postId = 1L;
        Long userId = 1L;
        User differentUser = createUser(2L, "userId", "pass", "");
        Post post = createPost(postId, differentUser, "새 내용 #springboot", "image.jpg");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when & then
        Assertions.assertThatCode(() -> sut.updatePost(postId, userId, createPostUpdateRequest()))
                .isInstanceOf(IllegalStatusException.class)
                .hasMessageContaining(String.format("사용자 (%d )는 post(%d ) 수정 권한이 없습니다.",userId, postId));

        // Then
        then(fileUploder).shouldHaveNoInteractions();
    }

    @DisplayName("포스트의 수정 정보를 입력하면, 포스트을 수정한다.")
    @Test
    void givenModifiedPostInfo_whenUpdatingArticle_thenUpdatesPost(){
        //given
        Long postId = 1L;
        Long userId = 1L;
        String expectedContent = "새 내용";

        Post post = createPost();
        PostUpdateRequest request = createPostUpdateRequest("새 내용#springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();
        Hashtag expectedHashtag = new Hashtag("springboot");
        expectedHashtags.add(expectedHashtag);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(hashtagFactory.createHashtag(anyString())).willReturn(expectedHashtags);
        given(hashtagRepository.getReferenceById(anyLong())).willReturn(expectedHashtag);
        willDoNothing().given(postRepository).flush();

        // When
        PostResponseWithHashtag result = sut.updatePost(postId, userId, request);

        // Then
        then(postRepository).should().findById(postId);
        then(hashtagFactory).should().createHashtag(anyString());
        then(postRepository).should().flush();
    }

    @DisplayName("포스트의 ID를 입력하면, 포스트를 삭제한다.")
    @Test
    void givenPostId_whenDeletingPost_thenDeletesPost() {
        // Given
        Long postId = 1L;
        Long userId = 1L;


        given(postRepository.findById(postId)).willReturn(Optional.of(createPost()));
        willDoNothing().given(postRepository).deleteByIdAndUser_Id(postId, userId);
        Hashtag value = new Hashtag("tag");
        given(hashtagRepository.getReferenceById(anyLong())).willReturn(value);


        // When
        sut.deletePost(postId, userId);

        // Then
        then(postRepository).should().findById(postId);
        then(postRepository).should().deleteByIdAndUser_Id(postId, userId);
    }
}
