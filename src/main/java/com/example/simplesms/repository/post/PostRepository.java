package com.example.simplesms.repository.post;

import com.example.simplesms.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PostRepository extends JpaRepository<Post, Long> {
    Integer countByUser_Id(Long userId);
    @Modifying
    void deleteByIdAndUser_Id(Long post, Long userId);

}
