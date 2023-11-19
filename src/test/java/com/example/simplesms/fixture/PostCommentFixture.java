package com.example.simplesms.fixture;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostComment;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.comment.CommentWriteRequest;

import static com.example.simplesms.fixture.PostFixture.createPost;
import static com.example.simplesms.fixture.UserFixture.createUser;

public class PostCommentFixture {

    public static CommentWriteRequest createCommentWriteRequest() {
        return new CommentWriteRequest(1L, 1L, "comment");
    }

    public static CommentWriteRequest createCommentWriteRequest(Long postId, Long userId, String comment) {
        return new CommentWriteRequest(postId, userId, comment);
    }

    public static PostComment createPostComment(Post post, User user, String content) {
        return new PostComment(post, user, content);
    }

    public static PostComment createPostComment() {
        return new PostComment(createPost(), createUser(), "comment");
    }

}
