package com.example.simplesms.repository.post;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.repository.post.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryCustom {
    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}
