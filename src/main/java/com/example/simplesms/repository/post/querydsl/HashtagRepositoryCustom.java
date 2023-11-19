package com.example.simplesms.repository.post.querydsl;

import java.util.List;

public interface HashtagRepositoryCustom {

    List<String> findLikeHashTagNameByUserId(Long userId);

    List<String> findHashtagNameByUserId(Long userId);

    List<String> findTop2ByUserId(Long userId);
}
