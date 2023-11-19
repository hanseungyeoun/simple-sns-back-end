package com.example.simplesms.fixture;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.post.PostResponse;
import com.example.simplesms.dto.post.PostResponseWithHashtag;
import com.example.simplesms.dto.post.PostUpdateRequest;
import com.example.simplesms.dto.post.PostWriteRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static com.example.simplesms.fixture.HashtagFixture.createHashtag;
import static com.example.simplesms.fixture.UserFixture.createUser;

public class PostFixture {

    public static Post createPost() {
        User user = createUser(1L, "email", "password", "nickName");
        Post post = Post.builder()
                .user(user)
                .content("content")
                .postImage("postImage")
                .hashtags(Set.of())
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        post.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));
        return post;
    }

    public static Post createPost(Long postId, User user ,String content, String imageName) {
        Post post = Post.builder()
                .user(user)
                .content(content)
                .postImage(imageName)
                .hashtags(Set.of())
                .build();

        ReflectionTestUtils.setField(post, "id", postId);
        return post;
    }

    public static PostWriteRequest createWriteRequest() {
        MockMultipartFile mockFile = MultipartFileFixture.createMockMultipartFile("file");
        return new PostWriteRequest("txt", mockFile);
    }

    public static PostWriteRequest createWriteRequest(String txt){
        MockMultipartFile mockFile = MultipartFileFixture.createMockMultipartFile("file");

        return new PostWriteRequest(txt, mockFile);
    }

    public static PostUpdateRequest createPostUpdateRequest() {
        MockMultipartFile mockFile = MultipartFileFixture.createMockMultipartFile("file");
        return new PostUpdateRequest("txt", mockFile);
    }

    public static PostUpdateRequest createPostUpdateRequest(String txt){
        MockMultipartFile mockFile = MultipartFileFixture.createMockMultipartFile("file");

        return new PostUpdateRequest(txt, mockFile);
    }

    public static PostResponse createPostResponse(){
        return new PostResponse(1L, "content", "postImage");
    }

    public static PostResponseWithHashtag createPostResponseWithHashtag(){
        return new PostResponseWithHashtag(1L, "content", "postImage", Set.of());
    }
}
