package com.example.simplesms.repository.post;

import com.example.simplesms.domain.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
