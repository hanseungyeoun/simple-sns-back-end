package com.example.simplesms.repository.post;

import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.post.PostLike;
import com.example.simplesms.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User userAccount);

    Integer countByUser_Id(Long userAccount);

    @Modifying
    @Query("delete from PostLike l where l.post.id in " +
            "(select p.id from Post p join p.user u where p.id = :postId and u.id = :userId)")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    void deleteByPost_IdAndAndUser_Id(@Param("postId") Long postId, @Param("userId") Long userId);

    void deleteByPost_Id(@Param("postId") Long postId);

}
