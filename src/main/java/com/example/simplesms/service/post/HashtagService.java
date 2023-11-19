package com.example.simplesms.service.post;

import com.example.simplesms.repository.post.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HashtagService {

    private HashtagRepository hashtagRepository;

    @Autowired
    public HashtagService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    public List<String> getTop2HashtagNames(Long userId) {
        return hashtagRepository.findTop2ByUserId(userId);
    }

    public List<String> getHashtagNames(Long userId) {
        return hashtagRepository.findHashtagNameByUserId(userId);
    }

    public List<String> getLikedHashtagNames(Long userId) {
        return hashtagRepository.findLikeHashTagNameByUserId(userId);
    }

}
